package com.es1.back.backingQuack.service

import com.es1.back.backingQuack.model.BackingTrackRequest
import org.jfugue.midi.MidiFileManager
import org.jfugue.pattern.Pattern
import org.jfugue.theory.ChordProgression
import org.jfugue.rhythm.Rhythm;
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID


@Service
class BackingTrackService {

    fun generateMidi(request: BackingTrackRequest): Pair<File, UUID> {
        val chordProgression = convertChordProgressionString(request.chordProgressionList)
        val cp = ChordProgression(chordProgression).setKey(request.root)
        val rhythm= Rhythm()
        rhythm.addLayer("O...").addLayer("````")
        val repeats = getNumberOfRepeats(request.chordProgressionList.toString())
        val pattern = Pattern(cp, rhythm.pattern.repeat(repeats)).setTempo(request.bpm)

        val uuid = UUID.randomUUID()

        val outputDir = Paths.get("output")
        if (!Files.exists(outputDir)) {
            Files.createDirectories(outputDir)
        }
        val localFilePath = outputDir.resolve("output_$uuid.mid")
        val midiFile = localFilePath.toFile()

        MidiFileManager.savePatternToMidi(pattern, midiFile)
        return Pair(midiFile, uuid)
    }



    private fun convertChordProgressionString(chordProgressionList: List<String>): String {
        val finalString = StringBuilder()
        for (chordProgression in chordProgressionList) {
            val chords = chordProgression.split(" ")
            for (chord in chords) {
                for (i in 1..4) {
                    finalString.append("$chord ")
                }
            }
        }
        return finalString.toString()
    }

    private fun getNumberOfRepeats(chordProgression: String): Int {
        val chords: List <String> = chordProgression.split(" ");
        return 2 * chords.size;
    }
}