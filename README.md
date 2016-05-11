# a code generator

Currently it can only do few things:

- define an entity in yml and:
  - generate a case class for the entity
  - generate the `ConnectionIO` operations for finders, create, update and delete for [Doobie](https://github.com/tpolecat/doobie) in an object.
  - use these to generate a repository trait using scala `Future`s
  - use these to generate a repository trait using scalaz's `Task`
- define bounds for an entity in yml and generate:
  - use these bounds to generate a bounds trait to check the entity using a `validate` method
  - generate a bounds trait using scala's `Future` as the return type
  - generate a bounds trait using scalaz's `Task` as the return type
- compose these two together

See the project in **src/sbt-test/codegen/allinone** for an example.

The nice thing is you can compose these operations together:

```
for {
  errors      <- validate(user)                  // returns a Task[Map[String, Seq[String]]] ... possibly scalaz's validation might fit better
  db          <- taskDb("test_composition")      // returns a Task[Transactor[Task]]
  allUsers    <- r.findAll()                     // returns a Task[List[User]]
  oUser1      <- r.findOne(1)                    // returns a Task[Option[User]]
  lUserBla    <- r.findByUsername("bla")         // returns a Task[List[User]]
  oUserBla    <- r.findOneByUsername("bla")      // returns a Task[Option[User]]
  rowsCreated <- r.create(user)                  // returns a Task[Int]
  rowsUpdated <- r.update(user)                  // returns a Task[Int]
  rowsDeleted <- r.delete(1)                     // returns a Task[Int]
} yield (???) // yield whatever you want

```

The full example for this can be found in the
[TaskCompositionSpec](src/sbt-test/codegen/allinone/src/test/scala/com/example/TaskCompositionSpec.scala).
test.

## The long term plan

Yesterday I learnt about the [eff monad](https://github.com/atnos-org/eff-scalaz)
for each of the operations and to create a framework, which makes composition of
these objects a charm! Since this is just a design pattern, I can keep all code
generators framework-agnostic and you're able to use whichever you like. I'll
split into separate sbt modules at some point. Of course you can always just not
provide yml files to not generate stuff.

## Yaml parsing

Currently I'm using snake-yaml which might blow up if I forget to map a potential
null-value into Option - i should check alternatives.

