// Cliente da API e sessão do cidadão, usados por todas as telas do app.
// Depende de config.js (API_URL, STATUS).

const SESSAO_CHAVE = "zela-sessao";

/** Chama a API e devolve o JSON; em erro, lança Error com a mensagem da API. */
async function api(caminho, { metodo = "GET", corpo } = {}) {
  let resposta;
  try {
    resposta = await fetch(API_URL + caminho, {
      method: metodo,
      headers: corpo ? { "Content-Type": "application/json" } : {},
      body: corpo ? JSON.stringify(corpo) : undefined,
    });
  } catch {
    throw new Error("Não foi possível falar com o servidor. A API está rodando em " + API_URL + "?");
  }
  if (resposta.status === 204) return null;
  const dados = await resposta.json().catch(() => null);
  if (!resposta.ok) {
    const erro = new Error(mensagemDeErro(dados, resposta.status));
    erro.status = resposta.status;
    throw erro;
  }
  return dados;
}

function mensagemDeErro(dados, status) {
  if (dados && dados.campos) return Object.values(dados.campos).join(". ");
  if (dados && dados.mensagem) return dados.mensagem;
  return "Erro " + status;
}

// ---------- sessão ----------

function lerSessao() {
  try { return JSON.parse(localStorage.getItem(SESSAO_CHAVE)); } catch { return null; }
}

function salvarSessao(cidadao) {
  localStorage.setItem(SESSAO_CHAVE, JSON.stringify(cidadao));
}

function sair() {
  localStorage.removeItem(SESSAO_CHAVE);
  location.href = "entrar.html";
}

/** Garante que há alguém logado; senão, manda para a tela de entrar (T01) e devolve null. */
function exigirLogin() {
  const cidadao = lerSessao();
  if (!cidadao) {
    location.replace("entrar.html");
    return null;
  }
  const nome = document.getElementById("usuario-nome");
  if (nome) nome.textContent = cidadao.nome.split(" ")[0];
  return cidadao;
}

// ---------- ajudas de tela ----------

function seloStatus(status) {
  const s = STATUS[status] || { rotulo: status, cor: "#5b6b63" };
  return `<span class="selo" style="background:${s.cor}">${s.rotulo}</span>`;
}

function dataBR(iso) {
  if (!iso) return "";
  const d = new Date(iso.length === 10 ? iso + "T12:00:00" : iso);
  return d.toLocaleDateString("pt-BR");
}

function dataHoraBR(iso) {
  return new Date(iso).toLocaleString("pt-BR", { dateStyle: "short", timeStyle: "short" });
}

function escapar(texto) {
  const div = document.createElement("div");
  div.textContent = texto ?? "";
  return div.innerHTML;
}

function mostrarErro(elemento, erro) {
  elemento.textContent = erro.message || String(erro);
  elemento.hidden = false;
}

/** Posição do GPS (RF03); se negar ou falhar, usa o centro padrão. */
function posicaoAtual() {
  return new Promise((resolve) => {
    if (!navigator.geolocation) return resolve({ lat: MAPA_CENTRO_PADRAO[0], lng: MAPA_CENTRO_PADRAO[1], gps: false });
    navigator.geolocation.getCurrentPosition(
      (p) => resolve({ lat: p.coords.latitude, lng: p.coords.longitude, gps: true }),
      () => resolve({ lat: MAPA_CENTRO_PADRAO[0], lng: MAPA_CENTRO_PADRAO[1], gps: false }),
      { timeout: 5000 }
    );
  });
}

/** Cria um mapa Leaflet com os mapas do OpenStreetMap. */
function criarMapa(idElemento, lat, lng, zoom = 16) {
  const mapa = L.map(idElemento).setView([lat, lng], zoom);
  L.tileLayer("https://tile.openstreetmap.org/{z}/{x}/{y}.png", {
    maxZoom: 19,
    attribution: "&copy; OpenStreetMap",
  }).addTo(mapa);
  return mapa;
}
