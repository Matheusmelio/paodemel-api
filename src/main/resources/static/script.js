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

const navItems = document.querySelectorAll(".nav-item");
const screens = document.querySelectorAll(".screen");
const screenTitle = document.querySelector("#screen-title");
const breadcrumbCurrent = document.querySelector("#breadcrumb-current");
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
const demoAuth = {
  token: "demo-gerente",
  nome: "Gerente Pao de Mel",
  email: "gerente@paodemel.com",
  perfil: "GERENTE",
  permissoes: rolePermissions.GERENTE
};
let isAuthenticated = false;
let currentRole = null;
let currentUser = null;

async function apiRequest(path, options = {}) {
  const response = await fetch(path, {
    headers: {
      "Content-Type": "application/json",
      ...(options.headers || {})
    },
    ...options
  });

  const data = await response.json().catch(() => ({}));
  if (!response.ok) {
    throw new Error(data.mensagem || "Não foi possível concluir a operação.");
  }

  return data;
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

function applyAuthState(auth, persistSession = true) {
  currentUser = auth;
  currentRole = auth.perfil;
  isAuthenticated = true;
  document.body.classList.remove("role-gerente", "role-atendente", "role-confeiteiro", "role-cliente");
  document.body.classList.add("authenticated", `role-${currentRole.toLowerCase()}`);
  if (persistSession) {
    localStorage.setItem("paodemel-auth", JSON.stringify(auth));
  }
  updateProfileView();
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

  const title = screenTitles[screenId] || "Pão de Mel & Cia";
  screenTitle.textContent = title;
  breadcrumbCurrent.textContent = title.replace("Gestão de ", "");
  window.scrollTo({ top: 0, behavior: "smooth" });
}

navItems.forEach((item) => {
  item.addEventListener("click", () => showScreen(item.dataset.screen));
});

document.querySelectorAll("[data-screen-link]").forEach((button) => {
  button.addEventListener("click", () => showScreen(button.dataset.screenLink));
});

const loginForm = document.querySelector("#login-form");
const loginButton = document.querySelector("#login-button");
const userRoleLabel = document.querySelector("#user-role-label");
const profileButton = document.querySelector("#profile-button");
const userAvatar = document.querySelector(".user-pill .avatar");
const profileRoleValue = document.querySelector("#profile-role-value");
const profileRoleDescription = document.querySelector("#profile-role-description");
const profilePermissions = document.querySelector("#profile-permissions");
const logoutButton = document.querySelector("#logout-button");

function updateProfileView() {
  const label = roleLabels[currentRole] || "Visitante";
  userRoleLabel.textContent = currentUser?.nome || label;
  if (userAvatar) {
    userAvatar.textContent = getInitials(currentUser?.nome);
  }
  profileRoleValue.textContent = label;
  profileRoleDescription.textContent = currentUser
    ? `Acesso ativo como ${label} (${currentUser.email}) no sistema Pão de Mel & Cia.`
    : `Acesso ativo como ${label} no sistema Pão de Mel & Cia.`;
  profilePermissions.innerHTML = "";

  const permissions = currentUser?.permissoes || rolePermissions[currentRole] || ["Faça login para visualizar permissões"];
  permissions.forEach((permission) => {
    const tag = document.createElement("span");
    tag.textContent = permission;
    profilePermissions.appendChild(tag);
  });
}

loginButton.addEventListener("click", async () => {
  if (!loginForm.reportValidity()) {
    return;
  }

  const loginData = new FormData(loginForm);
  loginButton.disabled = true;

  try {
    const auth = await apiRequest("/api/auth/login", {
      method: "POST",
      body: JSON.stringify({
        login: loginData.get("login"),
        senha: loginData.get("senha"),
        perfil: loginData.get("perfil")
      })
    });
    applyAuthState(auth);
    showToast(`Bem-vindo, ${auth.nome}.`);
    showScreen("dashboard");
  } catch (error) {
    showToast(error.message);
  } finally {
    loginButton.disabled = false;
  }
});

profileButton.addEventListener("click", () => showScreen("profile"));

logoutButton.addEventListener("click", () => {
  isAuthenticated = false;
  currentRole = null;
  currentUser = null;
  localStorage.removeItem("paodemel-auth");
  document.body.classList.remove("authenticated", "role-gerente", "role-atendente", "role-confeiteiro", "role-cliente");
  updateProfileView();
  showToast("Sessão encerrada com segurança.");
  showScreen("login");
});

try {
  const storedAuth = localStorage.getItem("paodemel-auth");
  applyAuthState(storedAuth ? JSON.parse(storedAuth) : demoAuth, false);
  showScreen("dashboard");
} catch (error) {
  applyAuthState(demoAuth, false);
  showScreen("dashboard");
}

const themeToggle = document.querySelector("#theme-toggle");

themeToggle.addEventListener("click", () => {
  document.body.classList.toggle("dark");
  themeToggle.textContent = document.body.classList.contains("dark") ? "Light mode" : "Dark mode";
});

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

  wizardPrev.disabled = currentWizardStep === 1;
  wizardNext.textContent = currentWizardStep === 3 ? "Salvar encomenda" : "Continuar";
}

wizardSteps.forEach((button) => {
  button.addEventListener("click", () => setWizardStep(Number(button.dataset.step)));
});

wizardNext.addEventListener("click", () => {
  if (currentWizardStep === 3) {
    showToast("Encomenda criada e enviada para produção.");
    showScreen("orders");
    return;
  }

  setWizardStep(currentWizardStep + 1);
});

wizardPrev.addEventListener("click", () => setWizardStep(currentWizardStep - 1));
setWizardStep(1);

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
  stockModal.classList.add("active");
  stockModal.setAttribute("aria-hidden", "false");
}

function closeModal() {
  stockModal.classList.remove("active");
  stockModal.setAttribute("aria-hidden", "true");
}

lowStockProduct.addEventListener("click", openModal);
modalClose.addEventListener("click", closeModal);
modalOk.addEventListener("click", closeModal);

stockModal.addEventListener("click", (event) => {
  if (event.target === stockModal) {
    closeModal();
  }
});

const toast = document.querySelector("#toast");
let toastTimeout;

function showToast(message) {
  clearTimeout(toastTimeout);
  toast.textContent = message;
  toast.classList.add("active");

  toastTimeout = setTimeout(() => {
    toast.classList.remove("active");
  }, 2800);
}

document.querySelector("#batch-button").addEventListener("click", () => {
  showToast("Fornada registrada. Estoque atualizado automaticamente.");
});

const registerProfile = document.querySelector("#register-profile");
const employeeField = document.querySelector(".employee-field");
const createAccountButton = document.querySelector("#create-account-button");
const registerForm = document.querySelector("#register-form");

function toggleEmployeeField() {
  const isEmployee = ["GERENTE", "ATENDENTE", "CONFEITEIRO"].includes(registerProfile.value);
  employeeField.classList.toggle("visible", isEmployee);
  employeeField.querySelector("input").required = isEmployee;
}

registerProfile.addEventListener("change", toggleEmployeeField);
toggleEmployeeField();

createAccountButton.addEventListener("click", async () => {
  if (!registerForm.reportValidity()) {
    return;
  }

  const registerData = new FormData(registerForm);
  const codigoInterno = registerData.get("codigoInterno");
  createAccountButton.disabled = true;

  try {
    await apiRequest("/api/auth/register", {
      method: "POST",
      body: JSON.stringify({
        nome: registerData.get("nome"),
        telefone: registerData.get("telefone"),
        email: registerData.get("email"),
        perfil: registerData.get("perfil"),
        senha: registerData.get("senha"),
        confirmarSenha: registerData.get("confirmarSenha"),
        codigoInterno: codigoInterno || null
      })
    });
    registerForm.reset();
    toggleEmployeeField();
    showToast("Cadastro criado com sucesso. Faça login para continuar.");
    showScreen("login");
  } catch (error) {
    showToast(error.message);
  } finally {
    createAccountButton.disabled = false;
  }
});

document.addEventListener("keydown", (event) => {
  if (event.key === "Escape") {
    closeModal();
  }
});
