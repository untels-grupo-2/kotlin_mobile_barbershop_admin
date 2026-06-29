# BarberHub Admin — App Móvil

App Android para administradores de la barbería. Permite gestionar reservas, barberos, servicios, horarios, usuarios, valoraciones y reportes. Recibe notificaciones push cuando llega una nueva reserva.

---

## Requisitos previos

| Herramienta | Versión mínima |
|-------------|----------------|
| Android Studio | Hedgehog (2023.1.1) o superior |
| JDK | 17 |
| Android SDK | API 35 (compileSdk) / API 24 mínimo (minSdk) |
| Kotlin | 1.9+ |

---

## Configuración inicial

### 1. Clonar el repositorio

```bash
git clone https://github.com/untels-grupo-2/kotlin_mobile_barbershop_admin.git
cd kotlin_mobile_barbershop_admin
```

### 2. Abrir en Android Studio

File → Open → seleccionar la carpeta del proyecto. Esperar a que Gradle sincronice.

### 3. Agregar `google-services.json`

El archivo `google-services.json` de Firebase **no se sube al repositorio**. Pedírselo al equipo y colocarlo en:

```
app/google-services.json
```

Sin este archivo el proyecto **no compila**.

### 4. Configurar la URL del backend

La URL base está definida en `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "BASE_URL", "\"http://10.0.2.2:8080/api/\"")
```

- `10.0.2.2` apunta al `localhost` de la máquina host desde el emulador de Android Studio.
- Si se usa un dispositivo físico, cambiar `10.0.2.2` por la IP local de la máquina donde corre el backend (ej. `192.168.1.x`).

### 5. Tener el backend corriendo

El backend debe estar levantado en el puerto `8080` antes de usar la app. Repositorio del backend:
[springboot_backend_barbershop](https://github.com/untels-grupo-2/springboot_backend_barbershop)

---

## Estructura del proyecto

```
app/
├── actividades/        # Activities (pantallas)
├── adapters/           # RecyclerView adapters
├── api/                # Interfaces Retrofit (ApiServices)
├── data/               # Entidades y DAOs de Room (caché local)
├── di/                 # Módulos de inyección de dependencias (Hilt)
├── domain/             # UseCases
├── dto/                # Modelos de request/response
├── repository/         # Repositorios (red + caché)
├── service/            # FirebaseMessagingService
├── util/               # Helpers (PreferenciasHelper, etc.)
└── viewmodel/          # ViewModels

shared-models/          # Módulo compartido con modelos y UiState
```

---

## Tecnologías utilizadas

- **Kotlin** — lenguaje principal
- **Hilt** — inyección de dependencias
- **Retrofit** — cliente HTTP
- **Coroutines + Flow** — programación asíncrona
- **Room** — base de datos local (caché offline)
- **Firebase Cloud Messaging (FCM)** — notificaciones push
- **Glide** — carga de imágenes
- **Material Design 3** — componentes UI

---

## Permisos requeridos

| Permiso | Uso |
|---------|-----|
| `INTERNET` | Comunicación con el backend |
| `POST_NOTIFICATIONS` | Mostrar notificaciones push (Android 13+) |

Al abrir la app por primera vez en Android 13 o superior, se solicitará el permiso de notificaciones.

---

## Notas

- La app usa `10.0.2.2:8080` como backend por defecto, que corresponde al `localhost` del emulador. Para dispositivo físico se debe cambiar la IP en `build.gradle.kts`.
- Las notificaciones push requieren que el token FCM del dispositivo esté registrado en el backend, lo cual ocurre automáticamente al hacer login.
