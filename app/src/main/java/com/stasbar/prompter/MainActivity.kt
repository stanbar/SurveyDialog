/*
 * Copyright (c) 2017 Stanislaw stasbar Baranski
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *          __             __
 *    _____/ /_____ ______/ /_  ____ ______
 *   / ___/ __/ __ `/ ___/ __ \/ __ `/ ___/
 *  (__  ) /_/ /_/ (__  ) /_/ / /_/ / /
 * /____/\__/\__,_/____/_.___/\__,_/_/
 *            stachu@stasbar.com
 */

package com.stasbar.prompter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.stasbar.feedbackdialog.FeedBackDialog
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button.setOnClickListener {
            FeedBackDialog(this)
                    .positiveButton(res = R.string.positive_text, iconRes = R.drawable.ic_android) {
                        Log.d("MainActivity", "positiveButton")
                    }
                    .negativeButton(res = R.string.negative_text, iconRes = R.drawable.ic_block) {
                        Log.d("MainActivity", "negativeButton")
                    }
                    .neutralButton(res = R.string.neutral){
                        Log.d("MainActivity", "neutral")
                    }
                    .constructiveFeedback { feedBackDialog, s ->
                        Log.d("MainActivity", "constructiveFeedback $s")
                    }
                    .iconsColor(R.color.colorPrimary)
                    .title(res = R.string.title)
                    .icon(iconRes = R.drawable.ic_beach)
                    .description(res = R.string.description)
                    .question(res = R.string.question)
                    .show()
        }


    }

    companion object {
        const val TAG = "FeedbackDialog"
    }
}
