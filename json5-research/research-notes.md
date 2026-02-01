# JSON5 Library Research

## Candidates to Evaluate

1. **json5-java** (Google)
   - Maven: com.google.json5:json5
   - GitHub: https://github.com/google/json5-java

2. **json5k** (Kotlin)
   - Maven: io.github.xosmig:json5k
   - Possible alternatives with similar names

3. **Other options**
   - Search Maven Central for additional options

## Evaluation Criteria

- ✅ Parse JSON5 input (comments, unquoted keys, trailing commas)
- ✅ Write JSON5 output (unquoted keys, trailing commas)
- ✅ Handle Kotlin types correctly
- 📅 Active maintenance
- 📚 Good documentation

## Findings


## Research Findings

### Available Libraries
1. **org.mvnpm:json5** (v2.2.3)
   - ❌ NPM package wrapped for Maven
   - ❌ Requires JavaScript engine (Nashorn/GraalVM)
   - ❌ Not suitable for our use case

2. **org.webjars.npm:json5** (v2.2.3)
   - ❌ Same as above - NPM wrapper
   - ❌ Not a pure JVM solution

3. **Pure JVM/Kotlin libraries**
   - ❌ None found on Maven Central
   - ❌ No actively maintained options

### Conclusion
**Recommended Approach**: Implement custom JSON5 writer + use Jackson for parsing

#### Parsing (Input)
- Use Jackson with lenient settings to parse JSON5-like input
- Jackson can handle:
  - Unquoted keys (JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
  - Single quotes (JsonParser.Feature.ALLOW_SINGLE_QUOTES)
  - Comments (JsonParser.Feature.ALLOW_COMMENTS)
  - Trailing commas (JsonParser.Feature.ALLOW_TRAILING_COMMA)

#### Writing (Output)
- Implement custom Json5Writer
- Features:
  - Unquoted keys for valid identifiers
  - Trailing commas
  - Single quotes for strings
  - Pretty-printing with indentation

#### Benefits
- No external JSON5 library dependency
- Full control over output format
- Leverages Jackson for robust parsing
- Simple to implement for our use case

