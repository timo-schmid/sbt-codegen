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

## A sample db-schema

An entity is an object to be stored in the database.

[src/main/db/user.yml](src/sbt-test/codegen/allinone/src/main/db/user.yml)

```
!entity
name: User
package: com.example.user
tableName: users                        # option
keys:
  id:
    type: int                           # default, can be overriden
fields:
  username:
    type: string
    length: 32
  firstName:
    type: string                        # required
    length: 255                         # length is optional, default is 255
  lastName:
    type: string
    length: 255
  email:
    type: string
    length: 255
    unique: true
  password:
    type: binary                        # for sha-256: char(64) would be feasable too
    length: 32                          # sha1 length / binary takes less space
  salt:
    type: binary
    length: 4
  lastLogin:
    type: datetime
  created:
    type: datetime
  updated:
    type: datetime
```

## A sample bounds-schema

A "bound" is used to validate something

[src/main/bounds/user.yml](src/sbt-test/codegen/allinone/src/main/bounds/user.yml)

```
!bound
name: UserBounds
package: com.example.user
type: User
validations:
  username:
    getter: "_.username"
    validator: com.example.validation.NonEmptyString
  firstName:
    getter: "_.firstName"
    validator: com.example.validation.NonEmptyString
  lastName:
    getter: "_.lastName"
    validator: com.example.validation.NonEmptyString
  email:
    getter: "_.email"
    validator: com.example.validation.ValidEmail
  password:
    getter: "_.password"
    validator: com.example.validation.ValidPassword
```

## Composition

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
[TaskCompositionSpec](src/sbt-test/codegen/allinone/src/test/scala/com/example/TaskCompositionSpec.scala)
test and some helper classes.

## The long term plan

Yesterday I learnt about the [eff monad](https://github.com/atnos-org/eff-scalaz)
which could be implemented for each of the operations and to create a framework,
which makes composition of these objects a charm! Since this is just a design
pattern, I can keep all code generators framework-agnostic and you're able to use
whichever you like wherever you like. I'll split into separate sbt modules at some
point. Of course you can always just not provide yml files to not generate stuff.

## Yaml parsing

Currently I'm using snake-yaml which might blow up if I forget to map a potential
null-value into Option - i should check alternatives.

## Programming this plugin

You can run the code generator in the **src/sbt-test/codegen/allinone** project by running
`sbt scripted`. That's it - it'll run the code generator (automatically invoked by compile),
check if the classes were all created and then run **test** in the project, which does some
simple specs2 tests.

