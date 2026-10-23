## **Guiding questions for the analysis of exceptions**

1. The root class `GameZoneException` can be declared as an `Exception` (checked) or as a `RuntimeException` (unchecked). Which of these two options is more appropriate for this project, and why? What impact does this have on the signatures of the methods in the services and repositories?

answ: `RuntimeException` is used because unchecked exceptions avoid the anti-pattern of cluttering method signatures with `throws` clauses that propagate through every layer. They allow input validation errors and business rule violations to interrupt the normal execution flow and travel directly to the top layer, without forcing intermediate layers to catch or declare them; this results in cleaner code and avoids tight coupling.

2. Why is it preferable to have a hierarchy of custom exceptions with a common root class (GameZoneException) rather than throwing a generic `IllegalArgumentException` in every case? Which design principles apply?

answ: This creates clear semantics: the exception type defines the nature of the error—such as malformed data or a missing record—rather than simply providing a text-based error message. This enables selective, controlled error handling within the UI and adheres to the Single Responsibility Principle (SRP), polymorphism, and GRASP principles.


3. The validators form a new layer (com.gamezone.validation). At what point in the flow are they invoked, and why is this placement consistent with the system's layered architecture? Can the validators access the persistence package? Justify your answer.

answ: It will be invoked at the beginning of methods within the service layer, just before any logic is executed—in the same place where standard exceptions are currently handled. This placement is logical, as the service layer is where most exceptions are likely to occur; it will therefore sit between the service and the model. It will not have access to the persistence layer, as it must not interact with either services or repositories. Any checks required by the exception must be performed by the service layer.


4. Refactoring must preserve the system's external behavior. What strategy does the team follow to ensure that the 10 menu operations continue to function exactly as before? What evidence is presented at the end to demonstrate that nothing has been broken?

answ: Refactor while preserving the original business semantics; conditions that previously triggered an `IllegalArgumentException` now throw a specific, typed exception. Include a comparison table of test cases that highlights the inputs, the original versus the refactored behavior, the workflow, and clearer error messages.

5. The `ConsoleMenu` class previously caught a single, generic exception. Now, it must catch four distinct types. Should this be implemented using a single `try` block with multiple `catch` clauses, nested `catch` blocks, or a general `catch (GameZoneException)`? Justify your choice by considering code clarity and the ability to provide differentiated messages to the user.

answ: A `try` block with multiple, sequential `catch` clauses, ordered from the most specific to the most general:
`catch (ResourceNotFoundException e)`

`catch (BusinessRuleException e)`

`catch (InvalidDataException e)`

`catch (PersistenceException e)`

`catch (GameZoneException e)` (a fallback for any other domain-specific exception)

`catch (Exception e)` (for unexpected system errors, preventing the application from crashing).

If only `GameZoneException` were caught, one would be forced to use `instanceof` to inspect the exception; this would violate the principle of polymorphism and clutter the exception handler. Using multiple `catch` blocks allows for a clear separation of responsibilities, ensuring that business logic remains distinct from what the user ultimately sees.