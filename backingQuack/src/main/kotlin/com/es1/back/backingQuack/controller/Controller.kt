package com.es1.back.backingQuack.controller

import com.es1.back.backingQuack.model.BackingTrackRequest
import com.es1.back.backingQuack.service.BackingTrackService
import org.slf4j.LoggerFactory
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api")
class Controller(private val backingTrackService: BackingTrackService) {
    private val logger = LoggerFactory.getLogger(Controller::class.java)

    @PostMapping("/generate/")
    fun generateMidi(@RequestBody request: BackingTrackRequest): ResponseEntity<InputStreamResource> {
        return try {
            logger.info(
                "Recebida requisição para gerar MIDI com progressão de acordes: {}",
                request.chordProgressionList
            )
            val (midiFile, uuid) = backingTrackService.generateMidi(request)
            val inputStream = midiFile.inputStream()
            val resource = InputStreamResource(inputStream)

            logger.info("Arquivo MIDI gerado com sucesso: output_{}.mid", uuid)
            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=output_${uuid}.mid")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource)
        } catch (e: Exception) {
            logger.error("Erro ao gerar o arquivo MIDI: {}", e.message, e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(e: ResponseStatusException): ResponseEntity<String> {
        logger.error("Erro: {}", e.message, e)
        return ResponseEntity.status(e.statusCode).body(e.reason)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<String> {
        logger.error("Erro inesperado: {}", e.message, e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado")
    }
}