// Endereço da API do Zela+ (backend/). Rode com: mvnw.cmd spring-boot:run
const API_URL = "http://localhost:8080/api";

// Centro padrão do mapa quando o GPS não estiver disponível (Capão Redondo, São Paulo)
const MAPA_CENTRO_PADRAO = [-23.6643, -46.7721];

// Rótulo e cor de cada status da ocorrência (RF06) — mesmos 7 estados da API
const STATUS = {
  ABERTA:      { rotulo: "Aberta",       cor: "#5b6b63" },
  VALIDADA:    { rotulo: "Validada",     cor: "#1d5fc4" },
  ENCAMINHADA: { rotulo: "Encaminhada",  cor: "#7a3fbf" },
  EM_EXECUCAO: { rotulo: "Em execução",  cor: "#b36b00" },
  CONCLUIDA:   { rotulo: "Concluída",    cor: "#1f7a4d" },
  RECUSADA:    { rotulo: "Recusada",     cor: "#c0392b" },
  UNIDA:       { rotulo: "Unida a outra", cor: "#7d8a83" },
};

// Ícone de cada categoria na tela de nova ocorrência (T03); as categorias vêm da API
const ICONES_CATEGORIA = {
  "Buraco na via": "🕳️",
  "Lâmpada queimada": "💡",
  "Lixo acumulado": "🗑️",
  "Calçada danificada": "🚧",
};
