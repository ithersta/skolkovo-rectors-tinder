import org.koin.dsl.module
import org.koin.ksp.generated.defaultModule

val module = module(createdAtStart = true) {
    includes(defaultModule)
    single { stateMachine(get()) }
}
