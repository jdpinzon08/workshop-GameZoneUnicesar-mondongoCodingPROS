# Diccionario del proyecto GameZone

Definiciones cortas basadas en los modelos, sus consumidores y el contrato de `docs/analysis.md` y `docs/class-diagram.md`.

## Dominio

| Término | Definición |
| --- | --- |
| `Product` (producto) | Clase abstracta con identificador, título, precio y existencias; es la base de los productos del catálogo. |
| `VideoGame` (videojuego) | Producto con plataforma, género y clasificación de edad. |
| `Console` (consola) | Producto con marca, modelo y generación. |
| `Accessory` (accesorio) | Producto abstracto que registra las consolas compatibles. |
| `Controller` (control) | Accesorio con tipo de conexión. |
| `Cable` (cable) | Accesorio con longitud y tipo de conector. |
| `Memory` (memoria) | Accesorio con capacidad en GB y tipo de memoria. |
| `Person` (persona) | Clase abstracta con identificador, nombre y teléfono; base de clientes y vendedores. |
| `Customer` (cliente) | Persona con correo e historial de compras. |
| `Seller` (vendedor) | Persona con código de empleado y turno de trabajo. |
| `Sale` (venta) | Transacción identificada por `saleId`, asociada a un cliente y vendedor, y que contiene productos. |
| `Return` (devolución) | Registro de devolución ligado a una venta y a los productos devueltos; incluye motivo y monto reembolsado. |
| `Warranty` (garantía) | Garantía abstracta asociada a una venta y a un producto, con fechas de inicio y vencimiento. |
| `BasicWarranty` (garantía básica) | Garantía de seis meses sin costo adicional. En el flujo de venta se asigna a las consolas sin garantía extendida. |
| `ExtendedWarranty` (garantía extendida) | Garantía de doce meses; su costo adicional es el 10 % del precio del producto. |
| Compatibilidad de producto | `Accessory.compatibleConsoles` contiene identificadores de consolas con las que se declara compatible el accesorio. |
| Productos de una venta | `Sale.products` es la lista de productos incluidos. Si un mismo producto aparece varias veces, el servicio lo cuenta como varias unidades y valida/rebaja el inventario por esa cantidad. |
| Total de venta | `Sale.calculateTotal()` suma el precio de cada elemento de la lista; `SaleService` añade los costos de garantías extendidas seleccionadas al registrar la venta. |
| Historial de compras del cliente | `Customer.purchaseHistory` contiene las ventas asociadas al cliente; `SaleService` agrega allí las ventas cargadas y las recién registradas. |

## Identificadores e inventario

| Término | Definición |
| --- | --- |
| ID de producto (`Product.id`, `productId`) | Identifica un producto del catálogo. Se usa para buscarlo y relacionarlo con listas de productos. |
| ID de venta (`Sale.saleId`) | Identifica una transacción; se usa para buscar la venta y asociarla con devoluciones y garantías. |
| ID de empleado (`Seller.employeeId`) | Código laboral propio del vendedor. `PersonService.findSellerById` busca por este código. |
| `Person.id` | Identificador general heredado por `Customer` y `Seller`; para el vendedor es distinto de `employeeId`. |
| `customerId` | Identificador de `Customer`; corresponde a su `Person.id` y permite buscar las ventas del cliente. |
| `stockQuantity` | Número actual de unidades disponibles de un producto en inventario. La venta lo reduce y una devolución lo restaura. |
| Cantidad de unidades | No hay un campo `quantity` en `Sale`: las unidades vendidas se representan repitiendo el producto en `Sale.products`. `stockQuantity` es inventario restante, no cantidad de la transacción. |

### Identificadores que no se deben confundir

| Comparación | Diferencia |
| --- | --- |
| `Person.id` y `Seller.employeeId` | `Person.id` identifica a la persona; `employeeId` es el código de empleado. Las búsquedas de ventas por vendedor usan `Seller.id`, mientras `findSellerById` usa `employeeId`. |
| `Product.id` y `Sale.saleId` | El primero identifica un artículo; el segundo, una transacción que agrupa artículos. |
| `customerId`, `saleId` y `productId` | Identifican, respectivamente, al cliente, a la venta y a un producto. No son intercambiables. |
| `stockQuantity` y unidades vendidas | `stockQuantity` es el saldo disponible. La lista `Sale.products`, contando repeticiones, expresa las unidades de esa venta. |

## Relaciones

| Relación | Significado |
| --- | --- |
| `Sale` – `Customer` | La venta guarda una referencia al cliente comprador; el cliente existe independientemente de la venta. |
| `Sale` – `Seller` | La venta guarda una referencia al vendedor que la atendió; el vendedor existe independientemente de la venta. |
| `Sale` – `Product` | La venta agrupa productos existentes en el catálogo; no es dueña de su ciclo de vida ni los elimina del inventario. |
| `Warranty` – `Sale` – `Product` | Cada garantía guarda referencias a la venta y al producto cubierto. Al registrar una venta, el servicio asigna garantía a cada consola. |
| `Return` – `Sale` – `Product` | Cada devolución guarda la venta original y la lista de productos devueltos; el servicio verifica que esos productos pertenezcan a la venta y restaura una unidad por elemento devuelto. |

## Promociones y descuentos

| Término | Definición |
| --- | --- |
| `Promotion` (promoción) | Clase abstracta con identificador, nombre y fechas de vigencia; declara el cálculo de descuento para una venta. |
| Promoción activa | `isActive(date)` indica si la fecha está dentro del intervalo de inicio y fin, inclusive. |
| `PercentageDiscount` | Promoción implementada que calcula un descuento como porcentaje del `Sale.totalAmount`. |
| `BulkPurchaseDiscount` y `CategoryDiscount` | Hay clases con esos nombres en el árbol de trabajo, pero están vacías; no tienen reglas de descuento implementadas. |
| Aplicación de promociones | El código actual no conecta `Promotion` con el registro de ventas ni aplica descuentos al total. |

## Arquitectura y requisitos

| Término | Definición |
| --- | --- |
| `Model` (modelo) | Capa de entidades del dominio y sus datos/comportamientos, como productos, personas y ventas. |
| `Service` (servicio) | Capa que aplica reglas y coordina operaciones entre modelos y repositorios. |
| `Repository` (repositorio) | Componente que carga y guarda entidades; los repositorios del proyecto trabajan con archivos CSV. |
| `Persistence` (persistencia) | Capa de lectura y escritura de datos, separada de las reglas del dominio. |
| `R1`, `R2`, `R3` y `R4` | No se encontró una definición de estos identificadores en el código ni en el contrato documental actual. Su significado queda sin establecer y no se puede asignar sin otra fuente del proyecto. |

> **Nota:** Las definiciones reflejan el código y el contrato disponible. Cuando ambos no establecen una regla o un significado, se indica expresamente en lugar de asumirlo.
