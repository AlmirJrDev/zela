package br.unasp.zela.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "zela.fotos.pasta=target/test-uploads")
@AutoConfigureMockMvc
class FotoControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void enviaEDevolveAFoto() throws Exception {
        byte[] bytes = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 1, 2, 3};
        MvcResult resposta = mvc.perform(multipart("/api/fotos")
                        .file(new MockMultipartFile("arquivo", "buraco.jpg", "image/jpeg", bytes)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.url").exists())
                .andReturn();

        String nome = com.jayway.jsonpath.JsonPath.read(resposta.getResponse().getContentAsString(), "$.nome");
        mvc.perform(get("/api/fotos/" + nome))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/jpeg"))
                .andExpect(content().bytes(bytes));
    }

    @Test
    void recusaArquivoQueNaoEImagem() throws Exception {
        mvc.perform(multipart("/api/fotos")
                        .file(new MockMultipartFile("arquivo", "nota.txt", "text/plain", "oi".getBytes())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Formato não aceito: envie JPEG, PNG ou WEBP"));
    }

    @Test
    void recusaFotoMaiorQue1Mb() throws Exception {
        byte[] grande = new byte[1024 * 1024 + 1];
        mvc.perform(multipart("/api/fotos")
                        .file(new MockMultipartFile("arquivo", "grande.jpg", "image/jpeg", grande)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("A foto deve ter no máximo 1 MB"));
    }

    @Test
    void fotoInexistenteDa404() throws Exception {
        mvc.perform(get("/api/fotos/00000000-0000-0000-0000-000000000000.jpg"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/fotos/..%2Fapplication.properties"))
                .andExpect(status().isNotFound());
    }
}
