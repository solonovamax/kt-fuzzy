/*
 * kt-fuzzy - A Kotlin library for fuzzy string matching
 * Copyright (c) 2023-2023 solonovamax <solonovamax@12oclockpoint.com>
 *
 * The file kt-fuzzy.dokka.gradle.kts is part of kotlin-fuzzy
 * Last modified on 16-09-2023 04:49 p.m.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * KT-FUZZY IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import ca.solostudios.dokkascript.plugin.DokkaScriptsConfiguration
import ca.solostudios.dokkascript.plugin.DokkaScriptsPlugin
import ca.solostudios.dokkastyles.plugin.DokkaStyleTweaksConfiguration
import ca.solostudios.dokkastyles.plugin.DokkaStyleTweaksPlugin
import java.time.Year
import org.jetbrains.dokka.DokkaConfiguration.Visibility
import org.jetbrains.dokka.base.DokkaBase
import org.jetbrains.dokka.base.DokkaBaseConfiguration
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
    id("org.jetbrains.dokka")
}

dependencies {
    dokkaPlugin(libs.dokka.plugin.script)
    dokkaPlugin(libs.dokka.plugin.style.tweaks)
}

tasks {
    val rootDokkaDirectory = rootProject.projectDir.resolve("dokka")
    val dokkaDirectories = if (project.rootProject != project)
        listOf(rootDokkaDirectory, project.projectDir.resolve("dokka"))
    else
        listOf(rootDokkaDirectory)

    val processDokkaIncludes by register<ProcessResources>("processDokkaIncludes") {
        from(dokkaDirectories.map { it.resolve("includes") })

        doFirst {
            val projectInfo = ProjectInfo(
                    group = project.group.toStringOrEmpty(),
                    module = project.name,
                    version = project.version.toStringOrEmpty(),
                                         )

            filter { line ->
                line.replace("☐", "<input type=\"checkbox\" readonly>")
                        .replace("☒", "<input type=\"checkbox\" readonly checked>")
                        .replace("\\[@ft-(\\w+)\\]".toRegex(), "<sup><a href=\"#footnote-\$1\">&#91;\$1&#93;</a></sup>")
                        .replace("\\[@ref-(\\d+)\\]".toRegex(), "<sup><a href=\"#reference-\$1\">&#91;\$1&#93;</a></sup>")
            }
            expand("project" to projectInfo)
        }

        destinationDir = buildDir.resolve("dokka/includes")
        group = JavaBasePlugin.DOCUMENTATION_GROUP
    }

    withType<AbstractDokkaTask>().configureEach {
        pluginConfiguration<DokkaBase, DokkaBaseConfiguration> {
            footerMessage = "© ${Year.now()} Copyright solo-studios"
            separateInheritedMembers = false
            customStyleSheets = rootDokkaDirectory.resolve("styles").listFiles()?.toList().orEmpty()
            customAssets = rootDokkaDirectory.resolve("assets").listFiles()?.toList().orEmpty()
            templatesDir = rootDokkaDirectory.resolve("templates")
        }
        pluginConfiguration<DokkaScriptsPlugin, DokkaScriptsConfiguration> {
            scripts = rootDokkaDirectory.resolve("scripts").listFiles()?.toList().orEmpty()
            remoteScripts = listOf(
                    // MathJax
                    "https://cdnjs.cloudflare.com/ajax/libs/mathjax/2.7.6/MathJax.js?config=TeX-AMS-MML_HTMLorMML&latest",
                                  )
        }
        pluginConfiguration<DokkaStyleTweaksPlugin, DokkaStyleTweaksConfiguration> {
            minimalScrollbar = true
            darkPurpleHighlight = true
            darkColorSchemeFix = true
            improvedBlockquoteBorder = true
            lighterBlockquoteText = true
            sectionTabFontWeight = "500"
            sectionTabTransition = true
            improvedSectionTabBorder = true
            disableCodeWrapping = true
            sidebarWidth = "340px"
        }

        moduleName = project.name
        moduleVersion = project.version.toStringOrEmpty()
    }

    withType<DokkaMultiModuleTask>().configureEach {
        if (project.rootProject == project)
            moduleName = project.name.formatAsName()
        else
            isEnabled = false
    }


    withType<DokkaTask>().configureEach {
        dependsOn(processDokkaIncludes)

        inputs.files(dokkaDirectories)

        dokkaSourceSets.configureEach {
            includes.from(processDokkaIncludes.outputs.files.asFileTree)

            reportUndocumented = true
            documentedVisibilities = setOf(Visibility.PUBLIC, Visibility.PROTECTED)
        }
    }

    withType<DokkaTaskPartial>().configureEach {
        dependsOn(processDokkaIncludes)

        inputs.files(dokkaDirectories)

        dokkaSourceSets.configureEach {
            includes.from(processDokkaIncludes.outputs.files.asFileTree)

            reportUndocumented = true
        }
    }
}
