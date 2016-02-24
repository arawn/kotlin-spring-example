Building REST services with Kotlin and Spring, JPA
==================================================

지난 2016년 2월 15일, 스프링 블로그에 [Developing Spring Boot applications with Kotlin](https://spring.io/blog/2016/02/15/developing-spring-boot-applications-with-kotlin) 라는 글이 올라왔다. [코틀린(Kotlin)](https://kotlinlang.org)이라는 언어로 스프링 기반 애플리케이션을 개발해보는것에 대한 글인데... 예제가 너무 간단해서 해당 코드만으로는 어떤 장점이 있는지 알기가 쉽지 않아 직접 예제를 만들어봤다.

직접 개발을 해보며 느꼈던 점과 겪었던 문제들을 간단히 기록해둔다.


코틀린(Kotlin)이란?
----------------

코틀린(Kotlin)은 Groovy, Scala등과 같이 JVM 플랫폼에서 동작하는 언어다.
IntelliJ IDE를 개발 및 판매하고 있는 젯브레인즈(Jetbrains)사에서 개발한 언어로 스위프트(Swift)와 무척 비슷한 모습을 가지고 있다. 최근 안드로이드를 개발자를 중심으로 Java를 대체할 수 있는 언어중 하나로 입소문(?)을 타고 있는듯 하다.

코틀린이 가진 뚜렷한 장점은 Java와 100% 호환성을 제공함에 따라 Java가 구축해놓은 오픈소스 생태계를 그대로 사용 할 수 있다는 것이다. 또한 Gradle, Maven과 같은 빌드 시스템도 쓸수 있고, 새로 만들어진 언어이니 만큼 Java에서 불편했던 것들이 많이 개선되었다. 무엇보다 IDE를 개발하는 회사가 만든 언어이기 때문에 IDE에 대한 지원 또한 강력하다.

코틀린 공식사이트에서 제공하는 [문서](https://kotlinlang.org/docs/reference/)와 Hazealign님이 번역한 [Android 개발을 수주해서 Kotlin을 제대로 써봤더니 최고였다.](https://gist.github.com/Hazealign/1bbc586ded1649a8f08f) 읽어본 후 이 예제를 만드는데 있어 큰 어려움은 없었고, 좋았던 것은 수다스러운 자바에 비해 간결해진 문법과 언어가 제공하는 몇가지 기능들로 인해 코드를 작성함에 있어 꽤 편하고 강력함을 만날 수 있었다는 점이다.

예제 설명 및 실행
-------------

사용자들이 주제(Topic)를 작성하고, 해당 주제에 대해 글(Post)을 공유하는 포럼(Forum) 웹 서비스를 예제로 만들었다. 크게 3가지 기능을 가지고 있다.

* 분류(Category) 조회
* 주제(Topic) 목록, 쓰기, 수정, 삭제
* 글(Post) 목록

### 개발환경

- Kotlin 1.0
- Frameworks: Spring Boot, Spring Web, Spring Data JPA
- Tools: Gradle 2.9, IntelliJ IDE 14


```
./gradlew clean bootRun
```

애플리케이션이 완전히 실행된 후 브라우저에서 `http://localhost:8080/swagger-ui#!/forum/` 페이지에 접속해 API를 테스트해보면 된다.

좋았던 점
-------

- 배우고, 시작하기가 쉬운 언어다. (Java와 호환성이 정말 좋다)
- [Properties support](https://kotlinlang.org/docs/reference/properties.html#declaring-properties)를 비롯해 타입추론, [Single-Expression function](https://kotlinlang.org/docs/reference/functions.html#single-expression-functions), [Smart Casts](https://kotlinlang.org/docs/reference/typecasts.html) 등으로 코드를 간결하게 작성한다.
- [확장 메소드](https://kotlinlang.org/docs/reference/extensions.html)로 코드 표현력 좋아진다.
- null 처리가 매우 안전하다. - [Null Safety](https://kotlinlang.org/docs/reference/null-safety.html)
- 강력한 IDE 지원!!! (Java 코드를 Kotlin 코드로 변환해주는 기능도 있다!)

겪었던 문제 또는 불편했던 점...
------------------------

### | CGLIB 기반 AOP는 올바르게 동작하지 않는다

코틀린으로 AOP를 사용하려면 인터페이스 기반(JDK Dynamic Proxy)이나 AspectJ Weaving 기법을 사용해야 한다. 원인을 파악해보기엔 시간적 여유가 없어 해결 방법만 찾아서 적용해두었다.

```java
@Service
@Transactional
class ForumService @Autowired constructor(var categoryRepository: CategoryRepository, var topicRepository: TopicRepository, var postRepository: PostRepository) {

}
```

위 코드는 인터페이스 없이 `@Transactional`을 사용해 트랜잭션 처리를 하려는 의도로 작성했지만, 그대로 실행하면 `@Autowired`가 무시되며 모든 Repository 빈들이 null이 된다. 위 정상적으로 실행하기 위해 `Load-Time Weaver`를 사용했다.

### | data class로 빈 검증(JSR-303)을 할 수 있는 방법을 못 찾았다

data class는 코틀린이 가진 멋진 언어적 장치이지만... 빈 검증을 할 수 있는 방법을 찾지 못 했다.

```java
@RestController
class TopicController {
	
	@RequestMapping("/write")
	fun writeTopic(@Valid form: TopicForm) { ... }

}

data class TopicForm(@NotEmpty var title:String? = null)
```

**TopicFrom** 객체의 title 속성은 빈(empty) 값을 가질 수 없다고 선언한 후 동작시켰지만 의도한대로 동작하지 않았다. 아래와 같이 일반 클래스로 변경한 후에는 정상적으로 동작했다.

```java
class TopicForm {
	@NotEmpty var title:String? = null	
}
```

### | Getter를 가진 Java Interface를 깔끔하게 구현하는 방법을 못 찾았다

Java와 Kotlin의 타입이 충돌하며 발생하는 문제인듯 한데... 깔끔한 방법을 찾지 못했다.

```java
// Java interface
public interface ErrorController {

	String getErrorPath();

}


// Kotlin implements
class DefaultErrorController : ErrorController {

	private var errorPath: String? = null
    override fun getErrorPath(): String? = errorPath

}
```

### | 스프링 내부에서 Property placeholders 를 사용하는 경우는... 울고싶다.

스프링은 `@Value("${property}")`을 사용해 값을 치환하는 방법을 사용하는데, 코틀린의 문자열 연산에서 `$`가 사용되기 때문에 `@Value("\${property}")`과 같이 이스케이프(\) 처리를 해야한다. 또는 Stack Overflow - [Change property placeholder signifier](http://stackoverflow.com/questions/33821043/spring-boot-change-property-placeholder-signifier/33883230#33883230) 글을 참고해서 문제를 해결할 수 있다.

문제는 아래와 같이 스프링 내부에서 사용하는 경우인데...

```java
package org.springframework.boot.autoconfigure.web;

public class ErrorProperties {

	@Value("${error.path:/error}")
	private String path = "/error";

}
```

프레임워크 코드를 변경 할 수 없기 때문에 `BeanPostProcessor`를 사용해 우회 처리했다.

```java
@Bean
    open fun errorPropertiesPostProcessor(@Value("\${error.path:/error}") errorPath: String): BeanPostProcessor {
        return object : BeanPostProcessor {
            override fun postProcessBeforeInitialization(bean: Any, beanName: String) = bean
            override fun postProcessAfterInitialization(bean: Any, beanName: String): Any {
                if (bean is ServerProperties) {
                    bean.error.path = errorPath
                }

                return bean
            }
        }
    }
```

정리
---

몇가지 문제점들이 있기는 했지만 어렵지 않게 슥슥 배워서 꽤 만족스럽게 예제를 만들어냈으니 무척이나 매력적인 언어인것 같다.
CGLIB 기반 AOP 문제만 해결된다면 당장이라도 운영 환경에서 써볼만 하다고 생각된다.