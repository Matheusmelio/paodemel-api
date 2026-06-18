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

function getAuthHeaders() {
  if (!currentRole) {
    return {};
  }

  return { "X-Perfil": currentRole };
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

  if (screenId === "orders") {
    loadOrders();
  }

  if (screenId === "batches") {
    loadFornadas();
  }

  if (screenId === "new-order") {
    prepareNewOrderForm();
  }

  window.scrollTo({ top: 0, behavior: "smooth" });
}

function statusBadgeClass(status) {
  const normalized = String(status || "").toLowerCase();

  if (normalized.includes("producao") && !normalized.includes("aguardando")) {
    return "production";
  }

  if (normalized.includes("pronto")) {
    return "ready";
  }

  if (normalized.includes("entregue")) {
    return "delivered";
  }

  return "waiting";
}

function formatStatusLabel(status) {
  const labels = {
    "Aguardando Producao": "Aguardando Produção",
    "Em Producao": "Em Produção",
    Pronto: "Pronto",
    Entregue: "Entregue",
    Cancelado: "Cancelado"
  };

  return labels[status] || status || "Aguardando Produção";
}

async function loadOrders() {
  const tbody = document.querySelector("#orders-table-body");

  if (!tbody || !isAuthenticated) {
    return;
  }

  tbody.innerHTML = '<tr><td colspan="6">Carregando encomendas...</td></tr>';

  try {
    const orders = await apiRequest("/api/encomendas", {
      headers: getAuthHeaders()
    });

    if (!Array.isArray(orders) || orders.length === 0) {
      tbody.innerHTML = '<tr><td colspan="6">Nenhuma encomenda cadastrada.</td></tr>';
      return;
    }

    tbody.innerHTML = "";

    orders.forEach((order) => {
      const row = document.createElement("tr");
      const badgeClass = statusBadgeClass(order.status);
      const statusLabel = formatStatusLabel(order.status);

      row.innerHTML = `
        <td>${order.codigo || "-"}</td>
        <td>${order.cliente || "-"}</td>
        <td>${order.massa || "-"}</td>
        <td>${order.recheio || "-"}</td>
        <td>${order.dataEntrega || "-"}</td>
        <td><span class="badge ${badgeClass}">${statusLabel}</span></td>
      `;

      tbody.appendChild(row);
    });
  } catch (error) {
    tbody.innerHTML = '<tr><td colspan="6">Nao foi possivel carregar as encomendas.</td></tr>';
    showToast(error.message);
  }
}

async function loadFornadas() {
  const list = document.querySelector("#batches-list");

  if (!list || !isAuthenticated) {
    return;
  }

  list.innerHTML = '<div><span>Carregando fornadas...</span><strong></strong></div>';

  try {
    const fornadas = await apiRequest("/api/fornadas", {
      headers: getAuthHeaders()
    });

    if (!Array.isArray(fornadas) || fornadas.length === 0) {
      list.innerHTML = '<div><span>Nenhuma fornada registrada.</span><strong></strong></div>';
      return;
    }

    list.innerHTML = "";

    fornadas.forEach((fornada) => {
      const item = document.createElement("div");
      item.innerHTML = `<span>${fornada.tipoPao || "Pao"}</span><strong>${fornada.quantidadeProduzida || 0} un. · ${fornada.horaSaida || "--:--"}</strong>`;
      list.appendChild(item);
    });
  } catch (error) {
    list.innerHTML = '<div><span>Nao foi possivel carregar as fornadas.</span><strong></strong></div>';
    showToast(error.message);
  }
}

function prepareNewOrderForm() {
  const clienteInput = document.querySelector("#order-cliente-nome");

  if (clienteInput && currentUser?.nome && !clienteInput.value.trim()) {
    clienteInput.value = currentUser.nome;
  }

  const dateInput = document.querySelector("#order-data-entrega");

  if (dateInput && !dateInput.value) {
    const deliveryDate = new Date();
    deliveryDate.setDate(deliveryDate.getDate() + 2);
    dateInput.value = deliveryDate.toISOString().slice(0, 10);
  }
}

function getOrderFormData() {
  const cliente = String(document.querySelector("#order-cliente-nome")?.value || "").trim()
    || String(currentUser?.nome || "").trim();
  const massa = String(document.querySelector("#order-massa")?.value || "").trim();
  const recheio = String(document.querySelector("#order-recheio")?.value || "").trim();
  const data = String(document.querySelector("#order-data-entrega")?.value || "").trim();
  const hora = String(document.querySelector("#order-hora-entrega")?.value || "15:00").trim();

  return {
    cliente,
    massa,
    recheio,
    dataEntrega: data && hora ? `${data} ${hora}` : data
  };
}

function validateWizardStep(step) {
  if (step === 1) {
    const cliente = String(document.querySelector("#order-cliente-nome")?.value || "").trim()
      || String(currentUser?.nome || "").trim();

    if (!cliente) {
      showToast("Informe o nome do cliente.");
      return false;
    }
  }

  if (step === 2) {
    const massa = String(document.querySelector("#order-massa")?.value || "").trim();
    const recheio = String(document.querySelector("#order-recheio")?.value || "").trim();

    if (!massa || !recheio) {
      showToast("Selecione massa e recheio.");
      return false;
    }
  }

  if (step === 3) {
    const data = String(document.querySelector("#order-data-entrega")?.value || "").trim();
    const hora = String(document.querySelector("#order-hora-entrega")?.value || "").trim();

    if (!data || !hora) {
      showToast("Informe data e hora de entrega.");
      return false;
    }
  }

  return true;
}

function resetWizardForm() {
  const clienteInput = document.querySelector("#order-cliente-nome");
  const telefoneInput = document.querySelector("#order-cliente-telefone");
  const whatsappInput = document.querySelector("#order-cliente-whatsapp");
  const dateInput = document.querySelector("#order-data-entrega");
  const horaInput = document.querySelector("#order-hora-entrega");

  if (clienteInput) {
    clienteInput.value = currentUser?.nome || "";
  }

  if (telefoneInput) {
    telefoneInput.value = "";
  }

  if (whatsappInput) {
    whatsappInput.value = "";
  }

  if (dateInput) {
    dateInput.value = "";
  }

  if (horaInput) {
    horaInput.value = "15:00";
  }

  setWizardStep(1);
}

async function saveOrder() {
  if (!validateWizardStep(3)) {
    return;
  }

  const payload = getOrderFormData();

  if (!payload.cliente || !payload.massa || !payload.recheio || !payload.dataEntrega) {
    showToast("Preencha cliente, massa, recheio e data de entrega.");
    return;
  }

  if (wizardNext) {
    wizardNext.disabled = true;
    wizardNext.textContent = "Salvando...";
  }

  try {
    const created = await apiRequest("/api/encomendas", {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify(payload)
    });

    showToast(`Encomenda ${created.codigo || ""} criada e enviada para produção.`.trim());
    resetWizardForm();
    await loadOrders();
    showScreen("orders");
  } catch (error) {
    showToast(error.message);
  } finally {
    if (wizardNext) {
      wizardNext.disabled = false;
      wizardNext.textContent = "Salvar encomenda";
    }
  }
}

async function registerFornada() {
  const button = document.querySelector("#batch-button");
  const tipoPao = String(document.querySelector("#batch-tipo-pao")?.value || "").trim();
  const quantidade = Number(document.querySelector("#batch-quantidade")?.value);
  const horaSaida = String(document.querySelector("#batch-hora-saida")?.value || "").trim();

  if (!tipoPao) {
    showToast("Selecione o tipo de pao.");
    return;
  }

  if (!Number.isFinite(quantidade) || quantidade <= 0) {
    showToast("Informe uma quantidade produzida maior que zero.");
    return;
  }

  if (!horaSaida) {
    showToast("Informe a hora de saida da fornada.");
    return;
  }

  if (button) {
    button.disabled = true;
    button.textContent = "Registrando...";
  }

  try {
    const response = await apiRequest("/api/fornadas", {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify({
        tipoPao,
        quantidadeProduzida: quantidade,
        horaSaida
      })
    });

    showToast(response.mensagem || "Fornada registrada. Estoque atualizado automaticamente.");
    await loadFornadas();
  } catch (error) {
    showToast(error.message);
  } finally {
    if (button) {
      button.disabled = false;
      button.textContent = "Registrar Fornada";
    }
  }
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
  const perfil = String(registerData.get("perfil") || "").trim();
  const payload = {
    nome: String(registerData.get("nome") || "").trim(),
    telefone: String(registerData.get("telefone") || "").trim(),
    email: String(registerData.get("email") || "").trim(),
    perfil,
    senha: String(registerData.get("senha") || ""),
    confirmarSenha: String(registerData.get("confirmarSenha") || ""),
    codigoInterno: codigoInterno || null,
    cpfCliente: onlyDigits(registerData.get("cpfCliente")),
    nascimento: String(registerData.get("nascimento") || "").trim() || null,
    sexo: String(registerData.get("sexo") || "").trim() || null,
    logradouro: String(registerData.get("logradouro") || "").trim() || null,
    numero: String(registerData.get("numero") || "").trim() || null,
    complemento: String(registerData.get("complemento") || "").trim() || null,
    cep: onlyDigits(registerData.get("cep")),
    bairro: String(registerData.get("bairro") || "").trim() || null,
    cidade: String(registerData.get("cidade") || "").trim() || null,
    estado: String(registerData.get("estado") || "").trim().toUpperCase() || null
  };

  if (!payload.nome || !payload.telefone || !payload.email || !payload.perfil || !payload.senha || !payload.confirmarSenha) {
    setAuthFeedback("Preencha todos os campos obrigatorios do cadastro.", "error");
    showToast("Preencha todos os campos obrigatorios do cadastro.");
    return;
  }

  if (payload.perfil === "CLIENTE" && !hasCompleteClientData(payload)) {
    setAuthFeedback("Para cliente, preencha CPF, nascimento, sexo, CEP e endereco completo.", "error");
    showToast("Preencha os dados completos do cliente.");
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
  const clientFields = document.querySelectorAll(".client-field");

  if (!registerProfile || !employeeField) {
    return;
  }

  const isEmployee = ["GERENTE", "ATENDENTE", "CONFEITEIRO"].includes(registerProfile.value);
  const isClient = registerProfile.value === "CLIENTE";
  employeeField.classList.toggle("visible", isEmployee);
  const input = employeeField.querySelector("input");
  if (input) {
    input.required = isEmployee;
  }

  clientFields.forEach((field) => {
    field.classList.toggle("visible", isClient);
    const control = field.querySelector("input, select");
    if (control) {
      control.required = isClient && control.name !== "complemento";
    }
  });
}

function onlyDigits(value) {
  return String(value || "").replace(/\D/g, "");
}

function hasCompleteClientData(payload) {
  return Boolean(
    payload.cpfCliente &&
    payload.cpfCliente.length === 11 &&
    payload.nascimento &&
    payload.sexo &&
    payload.logradouro &&
    payload.numero &&
    payload.cep &&
    payload.cep.length === 8 &&
    payload.bairro &&
    payload.cidade &&
    payload.estado &&
    payload.estado.length === 2
  );
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
  wizardNext.addEventListener("click", async () => {
    if (currentWizardStep === 3) {
      await saveOrder();
      return;
    }

    if (!validateWizardStep(currentWizardStep)) {
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
  batchButton.addEventListener("click", registerFornada);
}

document.addEventListener("keydown", (event) => {
  if (event.key === "Escape") {
    closeModal();
  }
});
