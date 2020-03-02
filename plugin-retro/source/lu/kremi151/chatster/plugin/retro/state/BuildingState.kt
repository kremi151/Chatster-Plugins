/**
 * Copyright 2020 Michel Kremer (kremi151)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package lu.kremi151.chatster.plugin.retro.state

class BuildingState {

    var line1: String? = null
    var line2: String? = null
    var line3: String? = null
    var backgroundStyle: BackgroundStyle? = null
    var textStyle: TextStyle? = null

    enum class BackgroundStyle(val code: Int, val value: String) {
        BASIC_RAINBOW(1, "basicRainbow"),
        COLOR_RAINBOW(2, "colorRainbow"),
        PALM_TRI(3, "palmTri"),
        PALM_CIRCLE(4, "palmCircle"),
        OUTLINE_TRI(5, "outlineTri")
    }

    enum class TextStyle(val code: Int, val value: String) {
        CYAN(1, "cyan"),
        RED_OUTLINED(2, "redOutlined"),
        RED_OUTLINED_THICK(3, "redOutlinedThick"),
        CHROME(4, "chrome")
    }

}