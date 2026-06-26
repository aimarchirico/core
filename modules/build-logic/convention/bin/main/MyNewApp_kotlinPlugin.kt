/**
 * Precompiled [my-new-app.kotlin.gradle.kts][MyNewApp_kotlin_gradle] script plugin.
 *
 * @see MyNewApp_kotlin_gradle
 */
public
class MyNewApp_kotlinPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("MyNewApp_kotlin_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
