const screenTitles = {
  dashboard: "Dashboard Principal",
  orders: "Gestão de Encomendas",
  "new-order": "Nova Encomenda",
  production: "Produção",
  batches: "Gestão de Fornadas",
  stock: "Estoque",
  pos: "Vendas de Balcão",
  reports: "Relatórios",
  admin: "Administração",
  profile: "Perfil",
  login: "Login",
  register: "Cadastro"
};

const publicScreens = new Set(["login", "register"]);
const managerOnlyScreens = new Set(["reports", "admin"]);
const roleLabels = {
  GERENTE: "Gerente",
  ATENDENTE: "Atendente de balcão",
  CONFEITEIRO: "Confeiteiro",
  CLIENTE: "Cliente final"
};
const rolePermissions = {
  GERENTE: ["Dashboard", "Encomendas", "Produção", "Fornadas", "Estoque", "Vendas", "Relatórios", "Administração"],
  ATENDENTE: ["Dashboard", "Encomendas", "Nova encomenda", "Estoque", "Vendas de balcão"],
  CONFEITEIRO: ["Dashboard", "Encomendas", "Produção", "Estoque"],
  CLIENTE: ["Dashboard", "Nova encomenda", "Timeline de pedidos"]
};

let isAuthenticated = false;
let currentRole = null;
let currentUser = null;
let toastTimeout;

const navItems = document.querySelectorAll(".nav-item");
const screens = document.querySelectorAll(".screen");
const screenTitle = document.querySelector("#screen-title");
const breadcrumbCurrent = document.querySelector("#breadcrumb-current");
const loginForm = document.querySelector("#login-form");
const loginButton = document.querySelector("#login-button");
const registerForm = document.querySelector("#register-form");
const createAccountButton = document.querySelector("#create-account-button");
const userRoleLabel = document.querySelector("#user-role-label");
const profileButton = document.querySelector("#profile-button");
const userAvatar = document.querySelector(".user-pill .avatar");
const profileRoleValue = document.querySelector("#profile-role-value");
const profileRoleDescription = document.querySelector("#profile-role-description");
const profilePermissions = document.querySelector("#profile-permissions");
const logoutButton = document.querySelector("#logout-button");
const toast = document.querySelector("#toast");
const authFeedbackItems = document.querySelectorAll(".auth-feedback");

function setAuthFeedback(message, type = "info") {
  if (!authFeedbackItems.length) {
    return;
  }

  authFeedbackItems.forEach((authFeedback) => {
    authFeedback.textContent = message;
    authFeedback.className = `auth-feedback ${type}`;
  });
}

function showToast(message) {
  if (!toast) {
    return;
  }

  clearTimeout(toastTimeout);
  toast.textContent = message;
  toast.classList.add("active");

  toastTimeout = setTimeout(() => {
    toast.classList.remove("active");
  }, 2800);
}

function safeSetSession(auth) {
  try {
    localStorage.setItem("paodemel-auth", JSON.stringify(auth));
  } catch (error) {
    // A sessão segue funcionando mesmo se o navegador bloquear localStorage.
  }
}

function safeRemoveSession() {
  try {
    localStorage.removeItem("paodemel-auth");
  } catch (error) {
    // Ignora bloqueio de armazenamento local.
  }
}

function getApiErrorMessage(data, response, rawBody) {
  if (data?.mensagem) {
    return data.mensagem;
  }

  if (data?.message) {
    return data.message;
  }

  if (data?.error) {
    return `${response.status}: ${data.error}`;
  }

  if (rawBody) {
    return `Erro ${response.status}: ${rawBody.slice(0, 180)}`;
  }

  return `Erro ${response.status} ao comunicar com a API.`;
}

function buildApiUrl(path) {
  return new URL(path, window.location.origin).toString();
}

async function apiRequest(path, options = {}) {
  let response;

  try {
    response = await fetch(buildApiUrl(path), {
      method: options.method || "GET",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
        ...(options.headers || {})
      },
      body: options.body
    });
  } catch (error) {
    throw new Error("Nao foi possivel conectar com a API. Verifique sua internet e tente novamente.");
  }

  const rawBody = await response.text();
  let data = {};

  if (rawBody) {
    try {
      data = JSON.parse(rawBody);
    } catch (error) {
      data = {};
    }
  }

  if (!response.ok) {
    throw new Error(getApiErrorMessage(data, response, rawBody));
  }

  return data;
}

function validateAuthResponse(auth) {
  if (!auth?.perfil || !auth?.email || !auth?.nome) {
    throw new Error("A API respondeu sem os dados de autenticacao esperados.");
  }
}

function getInitials(name) {
  if (!name) {
    return "PM";
  }

  return name
    .split(" ")
    .filter(Boolean)
    .slice(0, 2)
    .map((part) => part[0].toUpperCase())
    .join("");
}

function updateProfileView() {
  const label = roleLabels[currentRole] || "Visitante";

  if (userRoleLabel) {
    userRoleLabel.textContent = currentUser?.nome || label;
  }

  if (userAvatar) {
    userAvatar.textContent = getInitials(currentUser?.nome);
  }

  if (profileRoleValue) {
    profileRoleValue.textContent = label;
  }

  if (profileRoleDescription) {
    profileRoleDescription.textContent = currentUser
      ? `Acesso ativo como ${label} (${currentUser.email}) no sistema Pão de Mel & Cia.`
      : `Acesso ativo como ${label} no sistema Pão de Mel & Cia.`;
  }

  if (!profilePermissions) {
    return;
  }

  profilePermissions.innerHTML = "";
  const permissions = currentUser?.permissoes || rolePermissions[currentRole] || ["Faça login para visualizar permissões"];
  permissions.forEach((permission) => {
    const tag = document.createElement("span");
    tag.textContent = permission;
    profilePermissions.appendChild(tag);
  });
}

function applyAuthState(auth, persistSession = true) {
  validateAuthResponse(auth);
  currentUser = auth;
  currentRole = String(auth.perfil).toUpperCase();
  isAuthenticated = true;
  document.body.classList.remove("role-gerente", "role-atendente", "role-confeiteiro", "role-cliente");
  document.body.classList.add("authenticated", `role-${currentRole.toLowerCase()}`);

  if (persistSession) {
    safeSetSession(auth);
  }

  updateProfileView();
}

function clearAuthState() {
  isAuthenticated = false;
  currentRole = null;
  currentUser = null;
  safeRemoveSession();
  document.body.classList.remove("authenticated", "role-gerente", "role-atendente", "role-confeiteiro", "role-cliente");
  updateProfileView();
}

function updateAuthTabs(screenId) {
  if (!publicScreens.has(screenId)) {
    return;
  }

  document.querySelectorAll("[data-screen-link]").forEach((button) => {
    button.classList.toggle("active", button.dataset.screenLink === screenId);
  });
}

function showScreen(screenId) {
  if (!isAuthenticated && !publicScreens.has(screenId)) {
    showToast("Faça login para acessar o sistema.");
    screenId = "login";
  }

  if (isAuthenticated && managerOnlyScreens.has(screenId) && currentRole !== "GERENTE") {
    showToast("Acesso permitido apenas para Gerente.");
    screenId = "dashboard";
  }

  screens.forEach((screen) => {
    screen.classList.toggle("active", screen.id === screenId);
  });

  navItems.forEach((item) => {
    item.classList.toggle("active", item.dataset.screen === screenId);
  });

  updateAuthTabs(screenId);

  const title = screenTitles[screenId] || "Pão de Mel & Cia";
  if (screenTitle) {
    screenTitle.textContent = title;
  }
  if (breadcrumbCurrent) {
    breadcrumbCurrent.textContent = title.replace("Gestão de ", "");
  }

  window.scrollTo({ top: 0, behavior: "smooth" });
}

async function handleLogin(event) {
  event?.preventDefault();

  if (!loginForm || !loginButton) {
    return;
  }

  if (loginButton.disabled) {
    return;
  }

  if (!loginForm.reportValidity()) {
    return;
  }

  const loginData = new FormData(loginForm);
  const payload = {
    login: String(loginData.get("login") || "").trim(),
    senha: String(loginData.get("senha") || ""),
    perfil: String(loginData.get("perfil") || "").trim()
  };

  if (!payload.login || !payload.senha || !payload.perfil) {
    setAuthFeedback("Preencha e-mail, senha e perfil para continuar.", "error");
    showToast("Preencha e-mail, senha e perfil para continuar.");
    return;
  }

  setAuthFeedback("Verificando login...", "info");
  showToast("Verificando login...");
  loginButton.disabled = true;
  loginButton.textContent = "Entrando...";

  try {
    const auth = await apiRequest("/api/auth/login", {
      method: "POST",
      body: JSON.stringify(payload)
    });
    applyAuthState(auth);
    setAuthFeedback("Login realizado com sucesso.", "success");
    showToast(`Bem-vindo, ${auth.nome}.`);
    showScreen("dashboard");
  } catch (error) {
    setAuthFeedback(error.message, "error");
    showToast(error.message);
  } finally {
    loginButton.disabled = false;
    loginButton.textContent = "Entrar";
  }
}

async function handleRegister(event) {
  event?.preventDefault();

  if (!registerForm || !createAccountButton) {
    return;
  }

  if (createAccountButton.disabled) {
    return;
  }

  if (!registerForm.reportValidity()) {
    return;
  }

  const registerData = new FormData(registerForm);
  const codigoInterno = String(registerData.get("codigoInterno") || "").trim();
  const payload = {
    nome: String(registerData.get("nome") || "").trim(),
    telefone: String(registerData.get("telefone") || "").trim(),
    email: String(registerData.get("email") || "").trim(),
    perfil: String(registerData.get("perfil") || "").trim(),
    senha: String(registerData.get("senha") || ""),
    confirmarSenha: String(registerData.get("confirmarSenha") || ""),
    codigoInterno: codigoInterno || null
  };

  if (!payload.nome || !payload.telefone || !payload.email || !payload.perfil || !payload.senha || !payload.confirmarSenha) {
    setAuthFeedback("Preencha todos os campos obrigatorios do cadastro.", "error");
    showToast("Preencha todos os campos obrigatorios do cadastro.");
    return;
  }

  setAuthFeedback("Criando cadastro...", "info");
  showToast("Criando cadastro...");
  createAccountButton.disabled = true;
  createAccountButton.textContent = "Criando...";

  try {
    const auth = await apiRequest("/api/auth/register", {
      method: "POST",
      body: JSON.stringify(payload)
    });
    registerForm.reset();
    toggleEmployeeField();
    applyAuthState(auth);
    setAuthFeedback("Cadastro criado e salvo no banco de dados.", "success");
    showToast(`Cadastro criado. Bem-vindo, ${auth.nome}.`);
    showScreen("dashboard");
  } catch (error) {
    setAuthFeedback(error.message, "error");
    showToast(error.message);
  } finally {
    createAccountButton.disabled = false;
    createAccountButton.textContent = "Criar cadastro";
  }
}

function toggleEmployeeField() {
  const registerProfile = document.querySelector("#register-profile");
  const employeeField = document.querySelector(".employee-field");

  if (!registerProfile || !employeeField) {
    return;
  }

  const isEmployee = ["GERENTE", "ATENDENTE", "CONFEITEIRO"].includes(registerProfile.value);
  employeeField.classList.toggle("visible", isEmployee);
  const input = employeeField.querySelector("input");
  if (input) {
    input.required = isEmployee;
  }
}

function restoreSession() {
  try {
    const storedAuth = localStorage.getItem("paodemel-auth");
    if (!storedAuth) {
      return false;
    }

    applyAuthState(JSON.parse(storedAuth), false);
    showScreen("dashboard");
    return true;
  } catch (error) {
    safeRemoveSession();
    return false;
  }
}

navItems.forEach((item) => {
  item.addEventListener("click", () => showScreen(item.dataset.screen));
});

document.querySelectorAll("[data-screen-link]").forEach((button) => {
  button.addEventListener("click", (event) => {
    event.preventDefault();
    showScreen(button.dataset.screenLink);
  });
});

if (loginButton) {
  loginButton.addEventListener("click", handleLogin);
  window.paodemelLogin = handleLogin;
}

if (loginForm) {
  loginForm.addEventListener("submit", handleLogin);
}

if (createAccountButton) {
  createAccountButton.addEventListener("click", handleRegister);
  window.paodemelRegister = handleRegister;
}

if (registerForm) {
  registerForm.addEventListener("submit", handleRegister);
}

if (profileButton) {
  profileButton.addEventListener("click", () => showScreen("profile"));
}

if (logoutButton) {
  logoutButton.addEventListener("click", () => {
    clearAuthState();
    showToast("Sessão encerrada com segurança.");
    showScreen("login");
  });
}

const registerProfile = document.querySelector("#register-profile");
if (registerProfile) {
  registerProfile.addEventListener("change", toggleEmployeeField);
  toggleEmployeeField();
}

if (!restoreSession()) {
  clearAuthState();
  showScreen("login");
}

const themeToggle = document.querySelector("#theme-toggle");
if (themeToggle) {
  themeToggle.addEventListener("click", () => {
    document.body.classList.toggle("dark");
    themeToggle.textContent = document.body.classList.contains("dark") ? "Light mode" : "Dark mode";
  });
}

let currentWizardStep = 1;
const wizardSteps = document.querySelectorAll(".wizard-step");
const wizardContents = document.querySelectorAll(".wizard-content");
const wizardNext = document.querySelector("#wizard-next");
const wizardPrev = document.querySelector("#wizard-prev");

function setWizardStep(step) {
  currentWizardStep = Math.min(3, Math.max(1, step));

  wizardSteps.forEach((button) => {
    button.classList.toggle("active", Number(button.dataset.step) === currentWizardStep);
  });

  wizardContents.forEach((content) => {
    content.classList.toggle("active", Number(content.dataset.stepContent) === currentWizardStep);
  });

  if (wizardPrev) {
    wizardPrev.disabled = currentWizardStep === 1;
  }
  if (wizardNext) {
    wizardNext.textContent = currentWizardStep === 3 ? "Salvar encomenda" : "Continuar";
  }
}

if (wizardSteps.length) {
  wizardSteps.forEach((button) => {
    button.addEventListener("click", () => setWizardStep(Number(button.dataset.step)));
  });
}

if (wizardNext) {
  wizardNext.addEventListener("click", () => {
    if (currentWizardStep === 3) {
      showToast("Encomenda criada e enviada para produção.");
      showScreen("orders");
      return;
    }

    setWizardStep(currentWizardStep + 1);
  });
}

if (wizardPrev) {
  wizardPrev.addEventListener("click", () => setWizardStep(currentWizardStep - 1));
  setWizardStep(1);
}

let draggedCard = null;

document.querySelectorAll(".cake-card").forEach((card) => {
  card.addEventListener("dragstart", () => {
    draggedCard = card;
    card.classList.add("dragging");
  });

  card.addEventListener("dragend", () => {
    card.classList.remove("dragging");
    draggedCard = null;
  });
});

document.querySelectorAll(".kanban-column").forEach((column) => {
  column.addEventListener("dragover", (event) => {
    event.preventDefault();
    column.classList.add("drag-over");
  });

  column.addEventListener("dragleave", () => {
    column.classList.remove("drag-over");
  });

  column.addEventListener("drop", () => {
    column.classList.remove("drag-over");

    if (draggedCard) {
      column.appendChild(draggedCard);
      showToast("Status de produção atualizado em tempo real.");
    }
  });
});

const stockModal = document.querySelector("#stock-modal");
const lowStockProduct = document.querySelector("#low-stock-product");
const modalClose = document.querySelector("#modal-close");
const modalOk = document.querySelector("#modal-ok");

function openModal() {
  if (!stockModal) {
    return;
  }

  stockModal.classList.add("active");
  stockModal.setAttribute("aria-hidden", "false");
}

function closeModal() {
  if (!stockModal) {
    return;
  }

  stockModal.classList.remove("active");
  stockModal.setAttribute("aria-hidden", "true");
}

if (lowStockProduct) {
  lowStockProduct.addEventListener("click", openModal);
}

if (modalClose) {
  modalClose.addEventListener("click", closeModal);
}

if (modalOk) {
  modalOk.addEventListener("click", closeModal);
}

if (stockModal) {
  stockModal.addEventListener("click", (event) => {
    if (event.target === stockModal) {
      closeModal();
    }
  });
}

const batchButton = document.querySelector("#batch-button");
if (batchButton) {
  batchButton.addEventListener("click", () => {
    showToast("Fornada registrada. Estoque atualizado automaticamente.");
  });
}

document.addEventListener("keydown", (event) => {
  if (event.key === "Escape") {
    closeModal();
  }
});
