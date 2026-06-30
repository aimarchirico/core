package no.chirico.core.test

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.core.importer.ImportOption
import com.tngtech.archunit.library.Architectures.layeredArchitecture
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices
import org.junit.jupiter.api.Test

abstract class BaseArchitectureTest {

  private val classes: JavaClasses by lazy {
    ClassFileImporter()
      .withImportOption(ImportOption.DoNotIncludeTests())
      .importPackages(javaClass.packageName)
  }

  /** app -> feature -> core. */
  @Test
  fun `modules depend downward`() {
    layeredArchitecture()
      .consideringAllDependencies()
      .optionalLayer("App").definedBy("..app..")
      .optionalLayer("Feature").definedBy("..feature..")
      .optionalLayer("Core").definedBy("..core..")
      .whereLayer("App").mayNotBeAccessedByAnyLayer()
      .whereLayer("Feature").mayOnlyBeAccessedByLayers("App")
      .whereLayer("Core").mayOnlyBeAccessedByLayers("App", "Feature")
      .check(classes)
  }

  /** not feature A -> feature B */
  @Test
  fun `feature isolation`() {
    slices().matching("..feature.(*)..").should().notDependOnEachOther().check(classes)
  }

  /** controller -> service -> repository -> model. */
  @Test
  fun `layers depend downward`() {
    layeredArchitecture()
      .consideringAllDependencies()
      .optionalLayer("Controller").definedBy("..controller..")
      .optionalLayer("Service").definedBy("..service..")
      .optionalLayer("Repository").definedBy("..repository..")
      .optionalLayer("Model").definedBy("..model..")
      .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
      .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
      .whereLayer("Repository").mayOnlyBeAccessedByLayers("Controller", "Service")
      .whereLayer("Model").mayOnlyBeAccessedByLayers("Controller", "Service", "Repository")
      .check(classes)
  }
}
