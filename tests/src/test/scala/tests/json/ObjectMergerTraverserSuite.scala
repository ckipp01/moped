package tests.json

import moped.json.JsonElement
import munit.TestOptions
import tests.BaseSuite

class ObjectMergerTraverserSuite extends BaseSuite {
  def check(
      name: TestOptions,
      original: List[JsonElement],
      expected: JsonElement
  )(implicit loc: munit.Location): Unit = {
    test(name) {
      val obtained = JsonElement.merge(original)
      assertJsonEquals(obtained, expected)
    }
  }

  check(
    "single",
    List(parseJson("""{"a": {"b": 42}}""")),
    parseJson("""{"a": {"b": 42}}""")
  )

  check(
    "object-object",
    List(parseJson("""{"a": {"b": 42}}"""), parseJson("""{"a": {"c": 43}}""")),
    parseJson("""{"a": {"b": 42, "c": 43}}""")
  )

  check(
    "object-object",
    List(
      parseJson("""{"a": {"b": 42}}"""),
      parseJson("""{"a": {"b": {"c": 43}}}""")
    ),
    parseJson("""{"a": {"b": {"c": 43}}}""")
  )

  check(
    "object-object-object",
    List(
      parseJson("""{"a": {"b": {"a": 41}}}"""),
      parseJson("""{"a": {"b": {"b": 42}}}"""),
      parseJson("""{"a": {"b": {"c": 43}}}""")
    ),
    parseJson("""{"a": {"b": { "a": 41, "b": 42, "c": 43 }}}""")
  )

  check(
    "array-object",
    List(parseJson("""{"a": [41]}"""), parseJson("""{"a": {"b": 42}}""")),
    parseJson("""{"a": {"b": 42}}""")
  )

  check(
    "array-object",
    List(parseJson("""[41]"""), parseJson("""{"a": 42}""")),
    parseJson("""{"a": 42}""")
  )

  check(
    "array-array",
    List(parseJson("""{"a": [41]}"""), parseJson("""{"a": [42]}""")),
    parseJson("""{"a": [42]}""")
  )

  check(
    "object-element",
    List(parseJson("""{"a": [41]}"""), parseJson("""42""")),
    parseJson("""42""")
  )

  check(
    "array-element",
    List(parseJson("""[41]"""), parseJson("""42""")),
    parseJson("""42""")
  )

  check(
    "array-array",
    List(parseJson("""[41]"""), parseJson("""[42]""")),
    parseJson("""[42]""")
  )

  check(
    "element-object",
    List(parseJson("""42"""), parseJson("""{"a": [41]}""")),
    parseJson("""{"a": [41]}""")
  )

  check(
    "element-element",
    List(parseJson("""42"""), parseJson("""41""")),
    parseJson("""41""")
  )

  check(
    "number-boolean",
    List(parseJson("""{"a": 42}"""), parseJson("""{"a": true}""")),
    parseJson("""{"a": true}""")
  )

  check(
    "single-object",
    List(parseJson("""{"a": 42}""")),
    parseJson("""{"a": 42}""")
  )

  check(
    "positional",
    List(
      parseJson("""{"flag": true}"""),
      parseJson("""{"moped@positional": ["a", "b"]}""")
    ),
    parseJson("""|{
                 |  "flag": true,
                 |  "moped@positional": ["a", "b"]
                 |}
                 |""".stripMargin)
  )

}
