package com.es1.back.backingQuack.controller

import com.es1.back.backingQuack.model.BackingTrackRequest
import com.es1.back.backingQuack.service.BackingTrackService
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class Controller(private val backingTrackService: BackingTrackService) {

    @PostMapping("/generate/")
    fun generateMidi(@RequestBody request: BackingTrackRequest): ResponseEntity<InputStreamResource> {
        return try {
            val (midiFile,uuid) = backingTrackService.generateMidi(request)
            val inputStream = midiFile.inputStream()
            val resource = InputStreamResource(inputStream)

            ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=output_${uuid}.mid")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource)
        } catch (e: Exception) {
            ResponseEntity.status(500).build()
        }

    }
}