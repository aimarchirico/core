package no.chirico.core.test

import com.tngtech.archunit.base.DescribedPredicate
import com.tngtech.archunit.core.domain.JavaClass
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import java.io.File
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

abstract class BaseArchitectureTest(private val basePackage: String) {

  private val allClasses by lazy {
    ClassFileImporter()
      .withImportOption(ImportOption.DoNotIncludeTests())
      .importPackages(basePackage)
  }

  private fun allowedInternalPackages(vararg packages: String): DescribedPredicate<JavaClass> {
    return DescribedPredicate.describe("reside outside $basePackage or in allowed packages") {
      javaClass ->
      !javaClass.packageName.startsWith(basePackage) ||
        packages.any { javaClass.packageName.startsWith(it) }
    }
  }

  @Test
  fun `core domain should only depend on itself`() {
    classes()
      .that()
      .resideInAPackage("$basePackage.core.domain..")
      .should()
      .onlyDependOnClassesThat(allowedInternalPackages("$basePackage.core.domain"))
      .allowEmptyShould(true)
      .check(allClasses)
  }

  @Test
  fun `core repository should only depend on core domain`() {
    classes()
      .that()
      .resideInAPackage("$basePackage.core.repository..")
      .should()
      .onlyDependOnClassesThat(
        allowedInternalPackages("$basePackage.core.domain", "$basePackage.core.repository")
      )
      .allowEmptyShould(true)
      .check(allClasses)
  }

  @Test
  fun `core service should only depend on core modules`() {
    classes()
      .that()
      .resideInAPackage("$basePackage.core.service..")
      .should()
      .onlyDependOnClassesThat(allowedInternalPackages("$basePackage.core"))
      .allowEmptyShould(true)
      .check(allClasses)
  }

  @Test
  fun `feature modules should only depend core and feature modules`() {
    classes()
      .that()
      .resideInAPackage("$basePackage.feature..")
      .should()
      .onlyDependOnClassesThat(allowedInternalPackages("$basePackage.core", "$basePackage.feature"))
      .allowEmptyShould(true)
      .check(allClasses)
  }

  @Test
  fun `feature modules should be independent of each other`() {
    slices()
      .matching("$basePackage.feature.(*)..")
      .should()
      .notDependOnEachOther()
      .allowEmptyShould(true)
      .check(allClasses)
  }

  @Test
  fun `all Kotlin source files should use PascalCase naming`() {
    val rootDir = File(System.getProperty("user.dir")).parentFile ?: File(".")
    val sourceRoots = listOf("app", "core", "feature")
    val pascalCaseRegex = Regex("^[A-Z][a-zA-Z0-9]*\\.kt$")

    val violations =
      sourceRoots
        .map { File(rootDir, it) }
        .filter { it.exists() }
        .flatMap { root ->
          root
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" && it.path.contains("src/main/") }
            .filter { !pascalCaseRegex.matches(it.name) }
            .map { it.relativeTo(rootDir).path }
            .toList()
        }

    assertAll(violations.map { { throw AssertionError("File '$it' does not follow PascalCase") } })
  }

  @Test
  fun `all Kotlin source files should not exceed 300 lines`() {
    val rootDir = File(System.getProperty("user.dir")).parentFile ?: File(".")
    val sourceRoots = listOf("app", "core", "feature")
    val maxLines = 300

    val violations =
      sourceRoots
        .map { File(rootDir, it) }
        .filter { it.exists() }
        .flatMap { root ->
          root
            .walkTopDown()
            .filter { it.isFile && it.extension == "kt" && it.path.contains("src/main/") }
            .mapNotNull { file ->
              val lineCount = file.readLines().size
              if (lineCount > maxLines) {
                "${file.relativeTo(rootDir).path}: $lineCount lines (max: $maxLines)"
              } else {
                null
              }
            }
            .toList()
        }

    assertAll(violations.map { { throw AssertionError("File line count violation: $it") } })
  }
}
