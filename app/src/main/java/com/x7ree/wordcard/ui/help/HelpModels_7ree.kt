package com.x7ree.wordcard.ui.help

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class HelpContent(
    val content: List<HelpItem>
)

@Serializable
sealed class HelpItem {
    @Serializable
    @SerialName("header")
    data class Header(
        val title: String,
        val welcome: String
    ) : HelpItem()

    @Serializable
    @SerialName("step")
    data class Step(
        val stepNumber: String,
        val icon: String,
        val title: String,
        val description: String
    ) : HelpItem()

    @Serializable
    @SerialName("tips")
    data class Tips(
        val title: String,
        val content: String
    ) : HelpItem()

    @Serializable
    @SerialName("license")
    data class License(
        val title: String,
        val content: String
    ) : HelpItem()

    @Serializable
    @SerialName("acknowledgements")
    data class Acknowledgements(
        val title: String,
        val intro: String,
        val list: List<String>,
        val outro: String
    ) : HelpItem()

    @Serializable
    @SerialName("contact")
    data class Contact(
        val title: String,
        val intro: String,
        val urlLabel: String,
        val url: String,
        val description: String,
        val outro: String
    ) : HelpItem()

    @Serializable
    @SerialName("version")
    data class Version(
        val title: String,
        val versionLabel: String
    ) : HelpItem()
}
