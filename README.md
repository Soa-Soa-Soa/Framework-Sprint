# Framework Sprint - Documentation

Un framework Java léger pour le développement web, inspiré de Spring MVC.

## Structure du Projet
    src/
    ├── main/
    │   ├── java/
    │   │   └── com/
    │   │       └── framework/
    │   │           ├── controller/
    │   │           ├── modelview/
    │   │           ├── servlet/
    │   │           └── validation/
    │   └── webapp/
    │       └── WEB-INF/
    │           ├── views/
    │           └── web.xml

## Fonctionnalités

1. Routage et Contrôleurs
- Support des annotations `@Controller` et `@RestController`
- Mappings HTTP : `@GetMapping`, `@PostMapping`
- Support REST avec `@RestEndPoint`

```java
    @Controller
    public class UserController {
        @GetMapping("/users")
        public ModelView listUsers() {
            return new ModelView("users/list.jsp");
        }

        @PostMapping("/users/add")
        public ModelView addUser(@RequestParam String name) {
            // Logique métier
            return new ModelView("users/success.jsp");
        }
    }

2. Injection de Paramètres
    - Annotation @RequestParam pour les paramètres de requête
    - Support des types : String, Integer, Double, Boolean
    - Conversion automatique des types
        @PostMapping("/register")
        public ModelView register(
            @RequestParam String name,
            @RequestParam Integer age,
            @RequestParam Double rating
        ) {
            // ...
        }

3. Validation des Paramètres
    - Annotations de validation :
        @Required : Champ obligatoire
        @NotBlank : Chaîne non vide
        @Range(min, max) : Valeur numérique dans un intervalle
    - Messages d''erreur personnalisables
    - Validation multiple avec collecte de toutes les erreurs
        @PostMapping("/register")
        @ErrorURL("/register")
        public ModelView register(
            @RequestParam @NotBlank(message="Nom requis") String name,
            @RequestParam @Range(min=13, max=120) Integer age
        ) {
            // Code exécuté uniquement si la validation réussit
        }

4. Gestion des Sessions
    - Annotation @SessionInject pour l''injection de session
    - API Session simplifiée
        @Controller
        public class AuthController {
            @SessionInject
            private Session session;

            @PostMapping("/login")
            public ModelView login(@RequestParam String username) {
                session.setAttribute("user", username);
                return new ModelView("dashboard.jsp");
            }
        }

5. Gestion des Vues
        - Classe ModelView pour la gestion des vues et données
        - Support JSP avec transfert automatique des attributs
        - Conservation des données de formulaire en cas d''erreur
            ModelView mv = new ModelView("view.jsp");
            mv.addItem("user", user);
            mv.addItem("messages", messageList);
            return mv;

6. REST API
    - Support JSON automatique avec @RestController
    - Sérialisation/Désérialisation automatique
        @RestController
        public class ApiController {
            @GetMapping("/api/users")
            public List<User> getUsers() {
                return userService.findAll();
            }
        }

7. Gestion des Erreurs
    - Redirection automatique vers les pages d''erreur
    - Conservation des données de formulaire
    - Messages d''erreur personnalisables
        @ErrorURL("/form")
        public ModelView processForm(...) {
            // En cas d'erreur, redirige vers /form avec les données
        }

## Configuration
    -web.xml
        <servlet>
            <servlet-name>FrontController</servlet-name>
            <servlet-class>com.framework.servlet.FrontController</servlet-class>
            <init-param>
                <param-name>controllerPackage</param-name>
                <param-value>com.myapp.controllers</param-value>
            </init-param>
        </servlet>

## Dépendances Maven
    <dependencies>
        <dependency>
            <groupId>jakarta.servlet</groupId>
            <artifactId>jakarta.servlet-api</artifactId>
            <version>6.0.0</version>
        </dependency>
        <dependency>
            <groupId>org.reflections</groupId>
            <artifactId>reflections</artifactId>
            <version>0.10.2</version>
        </dependency>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
    </dependencies>

## Dépendances
    .Jakarta Servlet API
    .JSTL
    .Gson (pour JSON)
    .Reflections (pour le scan des packages)

## Version Minimale
    .Java 17
    .Tomcat 10.1
