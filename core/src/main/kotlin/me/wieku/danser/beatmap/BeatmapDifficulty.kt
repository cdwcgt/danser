package me.wieku.danser.beatmap

import javax.persistence.*

@Entity(name = "BeatmapDifficulty")
open class BeatmapDifficulty(
    var hpDrain: Float = 5f,
    var cs: Float = 5f,
    var od: Float = 5f,
    var ar: Float = 5f,
    var sliderMultiplier: Float = 1f,
    var sliderTickRate: Float = 1f
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    protected var id: Int? = null

}