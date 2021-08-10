/*
 * Copyright 2020 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.fhir.datacapture.views

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import com.google.android.fhir.datacapture.R
import com.google.android.fhir.datacapture.localizedPrefix
import com.google.android.fhir.datacapture.localizedText
import com.google.android.material.textfield.TextInputLayout
import org.hl7.fhir.r4.model.QuestionnaireResponse

internal object QuestionnaireItemMultiSelectViewHolderFactory :
  QuestionnaireItemViewHolderFactory(R.layout.questionnaire_item_multi_select_view) {
  override fun getQuestionnaireItemViewHolderDelegate() =
    @SuppressLint("StaticFieldLeak")
    object : QuestionnaireItemViewHolderDelegate {
      private lateinit var holder: Holder

      override fun init(itemView: View) {
        holder = Holder(itemView)
      }

      override fun bind(questionnaireItemViewItem: QuestionnaireItemViewItem) {
        val activity = holder.question.context.tryUnwrapContext()!!

        val (item, response, answersChangedCallback) = questionnaireItemViewItem

        holder.prefix.text = item.localizedPrefix
        holder.prefix.visibility = if (item.localizedPrefix.isNullOrEmpty()) GONE else VISIBLE

        holder.question.text = item.localizedText

        holder.summary.text = questionnaireItemViewItem.extractOptions().summaryText()
        val onClick =
          View.OnClickListener {
            activity.supportFragmentManager.setFragmentResultListener(
              MultiSelectFragment.RESULT_REQUEST_KEY,
              activity
            ) { _, result: Bundle ->
              val selectedIndices =
                result.getIntArray(MultiSelectFragment.RESULT_BUNDLE_KEY_SELECTED_INDICES)
                  ?: return@setFragmentResultListener

              val allAnswers = item.answerOption
              val selectedAnswers =
                selectedIndices.map { selectedIndex -> allAnswers[selectedIndex] }

              response.answer.clear()
              response.answer.addAll(
                selectedAnswers.map { answer ->
                  QuestionnaireResponse.QuestionnaireResponseItemAnswerComponent().apply {
                    value = answer.value
                  }
                }
              )
              holder.summary.text = questionnaireItemViewItem.extractOptions().summaryText()
              answersChangedCallback()
            }
            val fragment =
              MultiSelectFragment(
                title = item.localizedText ?: "",
                options = questionnaireItemViewItem.extractOptions(),
              )
            fragment.show(activity.supportFragmentManager, MultiSelectFragment.RESULT_REQUEST_KEY)
          }
        // We need to set the click-listener on both the summary TextView, and the endIcon (the
        // small downward-facing arrow on the right side of the container), so that clicks on both
        // views will open the dialog.
        holder.summary.setOnClickListener(onClick)
        holder.summaryHolder.setEndIconOnClickListener(onClick)
      }
    }

  private class Holder(itemView: View) {
    val prefix: TextView = itemView.findViewById(R.id.prefix)
    val question: TextView = itemView.findViewById(R.id.question)
    val summary: TextView = itemView.findViewById(R.id.multi_select_summary)
    val summaryHolder: TextInputLayout = itemView.findViewById(R.id.multi_select_summary_holder)
  }
}

/** Represents selectable options in the multi-select page. */
data class MultiSelectOption(val name: String, val selected: Boolean)

/** Summary of the selected options, used for displaying in the immutable summary TextView. */
private fun List<MultiSelectOption>.summaryText(): String {
  return filter { it.selected }.joinToString { it.name }
}

private fun QuestionnaireItemViewItem.extractOptions(): List<MultiSelectOption> {
  return questionnaireItem.answerOption.map { answerOption ->
    MultiSelectOption(
      name = answerOption.valueCoding.display,
      selected = hasAnswerOption(answerOption),
    )
  }
}
