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

package com.google.android.fhir.index.entities

import java.math.BigDecimal

/**
 * An index record for a quantity value in a resource.
 *
 * See https://hl7.org/FHIR/search.html#quantity.
 */
internal data class QuantityIndex(
  val name: String,
  val path: String,
  val system: String,
  val unit: String,
  val code: String,
  val value: BigDecimal,
  val canonicalCode: String,
  val canonicalValue: BigDecimal
)
