package indyscala.aug2013.model

/**
 * @author Ross A. Baker <baker@alumni.indiana.edu>
 */
case class Counter private (name: String)

object Counter {
  val Total: Counter = Counter("total")
  val JsonParseErrors: Counter = Counter("json-parse-errors")
  val CybozuNoFeatures: Counter = Counter("cybozu-no-features")
  val CybozuErrors: Counter = Counter("cybozu-errors")

  def language(lang: String, source: String): Counter = Counter(s"lang:${lang}:${source}")
}






