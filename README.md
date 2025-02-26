# Framework Web Java

## Configuration du Package des Contrôleurs

Pour spécifier le package qui contient vos contrôleurs, suivez ces étapes :

1. Ouvrez le fichier `src/main/webapp/WEB-INF/web.xml`

2. Localisez la section du FrontController :
```xml
<servlet>
    <servlet-name>FrontController</servlet-name>
    <servlet-class>com.framework.servlet.FrontController</servlet-class>
    <!-- La configuration du package se fait ici -->
</servlet>
```

3. Ajoutez ou modifiez le paramètre `controllerPackage` dans la section `init-param` :
```xml
<init-param>
    <param-name>controllerPackage</param-name>
    <param-value>com.votrepackage.controller</param-value>
</init-param>
```

### Exemples

#### Package unique
Pour scanner un seul package :
```xml
<init-param>
    <param-name>controllerPackage</param-name>
    <param-value>com.framework.controller</param-value>
</init-param>
```

### Structure du Projet
```
src/main/java/
    └── com/
        └── framework/
            └── controller/           # Package par défaut pour les contrôleurs
                └── TestController.java   # Exemple de contrôleur
```

### Vérification
Pour vérifier que votre configuration fonctionne :
1. Démarrez Tomcat
2. Accédez à `http://localhost:8080/sprint/app/`
3. Vous devriez voir la liste des contrôleurs trouvés dans le package spécifié

### Notes Importantes
- Le package spécifié doit exister dans votre projet
- Les classes dans ce package doivent être annotées avec `@Controller` pour être détectées
- Si le package n'est pas spécifié ou est invalide, une erreur sera affichée
