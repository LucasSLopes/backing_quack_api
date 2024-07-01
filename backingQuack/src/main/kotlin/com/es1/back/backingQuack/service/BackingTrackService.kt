package com.es1.back.backingQuack.service

import com.es1.back.backingQuack.model.BackingTrackRequest
import org.jfugue.midi.MidiFileManager
import org.jfugue.pattern.Pattern
import org.jfugue.theory.ChordProgression
import org.jfugue.rhythm.Rhythm
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID


@Service
class BackingTrackService {

    private val logger = LoggerFactory.getLogger(BackingTrackService::class.java)


    fun generateMidi(request: BackingTrackRequest): Pair<File, UUID> {
        logger.info(
            "Iniciando a geração do arquivo MIDI para a progressão de acordes: {}",
            request.chordProgressionList
        )

        return try {
            val chordProgression = convertChordProgressionString(request.chordProgressionList)
            logger.debug("Progressão de acordes convertida: {}", chordProgression)

            val cp = ChordProgression(chordProgression).setKey(request.root)
            val rhythm = Rhythm()
            rhythm.addLayer("O...").addLayer("````")
            val repeats = getNumberOfRepeats(request.chordProgressionList.toString())
            val pattern = Pattern(cp, rhythm.pattern.repeat(repeats)).setTempo(request.bpm)

            val uuid = UUID.randomUUID() //Gera um id único para cada arquivo criado

            val outputDir = Paths.get("output")
            if (!Files.exists(outputDir)) {
                Files.createDirectories(outputDir)
                logger.info("Diretório de saída criado: {}", outputDir)
            }

            val localFilePath = outputDir.resolve("output_$uuid.mid")
            val midiFile = localFilePath.toFile()

            MidiFileManager.savePatternToMidi(pattern, midiFile)
            Pair(midiFile, uuid)
        } catch (e: Exception) {
            logger.error("Erro ao gerar o arquivo MIDI: {}", e.message, e)
            throw RuntimeException("Erro ao gerar o arquivo MIDI", e)
        }

    }

    private fun convertChordProgressionString(chordProgressionList: List<String>): String {
        return try {
            val finalString = StringBuilder()
            for (chordProgression in chordProgressionList) {
                val chords = chordProgression.split(" ")
                for (chord in chords) {
                    repeat(4) {
                        finalString.append("$chord ")
                    }
                }
            }
            val longFinalString = StringBuilder().apply {
                repeat(15) {
                    append(finalString)
                }
            }
            logger.debug("String final da progressão de acordes: {}", longFinalString)
            longFinalString.toString()
        } catch (e: Exception) {
            logger.error("Erro ao converter a progressão de acordes: {}", e.message, e)
            throw RuntimeException("Erro ao converter a progressão de acordes", e)
        }
    }

    private fun getNumberOfRepeats(chordProgression: String): Int {
        return try {
            val chords: List<String> = chordProgression.split(" ")
            val repeats = 2 * chords.size
            logger.debug("Número de repetições calculado: {}", repeats)
            repeats

        } catch (e: Exception) {
            logger.error("Erro ao calcular o número de repetições: {}", e.message, e)
            throw RuntimeException("Erro ao calcular o número de repetições", e)
        }

    }
}