package de.egor.culturalfootprint

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.util.stream.Collectors.joining

class ShortestPalindromeTest {

  private val shortestPalindromeBuilder = ShortestPalindromeBuilder();

  @CsvSource("aacecaaa,aaacecaaa", "abcd,dcbabcd", "dcbabcde,edcbabcde", "'',''",
  "baabcd,dcbaabcd", "baaabcd,dcbaaabcd", "aabbaad,daabbaad")
  @ParameterizedTest
  internal fun `should return shortest palindrome`(input: String, expectedResult: String) {
    assertThat(shortestPalindromeBuilder.build(input)).isEqualTo(expectedResult)
  }
}

class ShortestPalindromeBuilder {

  fun build(input: String): String {
    if (input.isEmpty() || input.reversed() == input) {
      return input
    }
    val preLast = makePreLast(input)
    val last = makeLast(input)
    return if (preLast.length < last.length) preLast else last
  }

  private fun makePreLast(input: String): String {
    val lastElementIndex = input.length - 1
    val elements = mutableListOf<Char>()
    val backwardElements = mutableListOf<Char>()

    for (i in lastElementIndex downTo 0 step 1) {
      val currentElement = input[i]
      if (backwardElements.isNotEmpty()) {
        if (elements.isEmpty()) {
          backwardElements.add(currentElement)
          elements.addAll(backwardElements)
          backwardElements.clear()
          continue
        }
        if (currentElement == elements.last()) {
          backwardElements.add(currentElement)
          elements.removeAt(elements.lastIndex)
          continue
        }
        elements.addAll(backwardElements)
        elements.add(currentElement)
        backwardElements.clear()
        continue
      }
      val preLastElement = elements.preLast()
      if (preLastElement == currentElement) {
        backwardElements.add(currentElement)
        backwardElements.add(elements.last())
        backwardElements.add(preLastElement)
        elements.removeAt(elements.lastIndex)
        elements.removeAt(elements.lastIndex)
        continue
      }
      elements.add(currentElement)
    }
    if (backwardElements.isEmpty()) {
      return input.reversed() + input.substring(1)
    }
    val resultBuilder = StringBuilder()
    resultBuilder.append(joinAsString(elements))
    val backwardElementsReversed = backwardElements.reversed()
    resultBuilder.append(joinAsString(backwardElementsReversed))
    resultBuilder.append(joinAsString(backwardElements.subList(3, backwardElements.size)))
    resultBuilder.append(joinAsString(elements.reversed()))
    return resultBuilder.toString()
  }

  private fun makeLast(input: String): String {
    val lastElementIndex = input.length - 1
    val elements = mutableListOf<Char>()
    val backwardElements = mutableListOf<Char>()

    for (i in lastElementIndex downTo 0 step 1) {
      val currentElement = input[i]
      if (backwardElements.isNotEmpty()) {
        if (elements.isEmpty()) {
          backwardElements.add(currentElement)
          elements.addAll(backwardElements)
          backwardElements.clear()
          continue
        }
        if (currentElement == elements.last()) {
          backwardElements.add(currentElement)
          elements.removeAt(elements.lastIndex)
          continue
        }
        elements.addAll(backwardElements)
        elements.add(currentElement)
        backwardElements.clear()
        continue
      }
      val lastElement = elements.lastOrNull()
      if (lastElement == currentElement) {
        backwardElements.add(currentElement)
        backwardElements.add(elements.last())
        elements.removeAt(elements.lastIndex)
        continue
      }
      elements.add(currentElement)
    }
    if (backwardElements.isEmpty()) {
      return input.reversed() + input
    }
    val resultBuilder = StringBuilder()
    resultBuilder.append(joinAsString(elements))
    val backwardElementsReversed = backwardElements.reversed()
    resultBuilder.append(joinAsString(backwardElementsReversed))
    resultBuilder.append(joinAsString(backwardElements.subList(2, backwardElements.size)))
    resultBuilder.append(joinAsString(elements.reversed()))
    return resultBuilder.toString()
  }

  private fun joinAsString(elements: List<Char>): String {
    return elements.stream()
        .map { it.toString() }
        .collect(joining())
  }

  fun <T> MutableList<T>.preLast(): T? = if (this.size > 1) this[this.size - 2] else null
}
