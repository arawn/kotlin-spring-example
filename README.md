Building REST services with Kotlin and Spring, JPA
==================================================

> 약 1년전 작성했던 예제를 2017년 5월을 기준으로 보완해본다.

2017년 5월 17일, 구글이 안드로이드 공식 언어로 코틀린(Kotlin)을 추가했다고 발표했다. 작년 2월에 1.0 정식 발표 후 호기심에 예제를 만들어 보았고, 매력적인 언어라고 평가했었다. 개인적으로 JVM 플랫폼 기반 시장의 한 축으로 자리를 잡을 거라고 생각했었는데, 1년이 조금 지난 시점에 멋진 결과를 만들어냈다고 생각한다. 코틀린 팀은 공식 블로그의 [Kotlin on Android. Now official](https://blog.jetbrains.com/kotlin/2017/05/kotlin-on-android-now-official/)를 통해, 코틀린의 비전은 풀스택 웹 애플리케이션, 안드로이드(Android)와 iOS 앱, 임베디드(embedded)/IoT 등 다양한 플랫폼에서 코틀린으로 개발할 수 있도록 하는 것이라고 말했다.

작년 2월 스프링 블로그에서 올라왔던 [Developing Spring Boot applications with Kotlin](https://spring.io/blog/2016/02/15/developing-spring-boot-applications-with-kotlin) 이후 한동안 잠잠했던, 스프링 팀도 올해 1월에 [스프링 프레임워크 5.0이 코틀린을 지원](https://spring.io/blog/2017/01/04/introducing-kotlin-support-in-spring-framework-5-0)할 것이라고 소개했다.
외부로 드러내진 않았지만, 코틀린의 이슈 트래커나 코틀린과 관련된 오픈소스 프로젝트들을 쫓아다니다 보면 스프링 팀의 커미터들에 글을 종종 볼 수 있다.

작년에 예제를 만들면서 아쉬웠던 점들이 해결이 되었는지 궁금해, 예제를 보완해봤다.

개발환경 변경사항
------------

- 코틀린(Kotlin) 버전 변경: 1.0.0 -> 1.1.2-2
- 스프링 부트(Spring Boot) 버전 변경: 1.3.2 -> 1.5.3
- 스웨거(Swagger) 버전 변경: 2.1.4 -> 3.0.5
- 핸들바(Handlebars) 제거


작년에 겪었던 문제 또는 불편했던 점...
------------------------------

### | CGLIB 기반 AOP는 올바르게 동작하지 않는다

이 현상은 코틀린의 언어 설계 원칙과 연관이 있다. 아래 내용은 코틀린 [공식문서](https://kotlinlang.org/docs/reference/classes.html#inheritance)에 있는 내용이다.

> By default, all classes in Kotlin are final, which corresponds to Effective Java, Item 17: Design and document for inheritance or else prohibit it.
>
> Effective Java, Item 17: 상속에 대한 설계와 문서화를 제대로 하지 않을 거면 아예 상속을 허용하지 말라.

코틀린은 상속에 대해 명확하게 작성하기를 바라기 때문에 필요하다면 open 지시어를 사용하라고 되어 있다.
그렇기에 AOP를 적용하려는 대상에 open 지시어를 선언해주어야 한다.

```kotlin
@Service
@Transactional
open class ForumService constructor(var categoryRepository: CategoryRepository) {

    open fun categories() = categoryRepository.findAll()

}
```

대상에는 클래스(class)뿐만이 아니라 메소드(method), 필드(field) 등이 모두 포함된다.

스프링 프레임워크를 기반으로 작성하는 애플리케이션에서 AOP는 매우 광범위하게 사용되기 때문에 실무자의 입장에서는 꽤 번거로운 제약사항이다.
이 제약사항에 대해 개방(open)파와 폐쇄(final)파가 [다양한 의견을 나누는 글](https://discuss.kotlinlang.org/t/classes-final-by-default/166/2)이 있으니, 관심이 있다면 읽어보자.

어떤 과정을 거쳐 의사결정이 있었는지 알 수 없지만, 코틀린 팀은 [Compiler Plugins](https://kotlinlang.org/docs/reference/compiler-plugins.html#kotlin-spring-compiler-plugin)을 통해 해결책을 제시한듯 하다.
애노테이션으로 컴파일 시점에 필요한 작업을 끼워넣는 방식이며, 이런 형태의 유명한 도구로 [롬복](https://projectlombok.org)이 있다.

현재 두 개의 플러그인(All-open 플러그인, No-arg 플러그인)이 제공되고 있고, 사용 방법은 빌드 도구(Gradle, Maven)에 몇가지 설정만 추가하면 된다.

All-open 플러그인 적용 후 open 지시어를 일일히 쓰지 않아도 AOP가 잘 동작하는걸 확인 할 수 있었고, 그리고 No-arg 플러그인 덕분에 하이버네이트 엔티티에 기본 생성자(default constructor)를 작성해주지 않는 편리함도 얻었다.


```kotlin
@Entity
data class Category( var name: String
                   , val createdAt: Date = Date()) {


    // No-arg 플러그인 적용 후 기본 생성자 제거
    //
    // for hibernate
    // private constructor() : this("")

}
```

### | data class로 빈 검증(JSR-303)을 할 수 있는 방법을 못 찾았다

코틀린이 자바와의 호환성을 유지하기 위해 제공하는 기능인 [Annotation Use-site Targets](https://kotlinlang.org/docs/reference/annotations.html#annotation-use-site-targets)만으로 깔끔하게 불편함이 해소되었다.

```kotlin
data class TopicForm (
    @field:NotEmpty
    var title: String = "",

    @field:NotEmpty
    var password: String = "",

    @field:NotEmpty
    var author: String = ""
)
```

어렴풋한 기억으로 코틀린 컴파일러 초기 시절에는 애노테이션을 처리하는 방식으로 인해 발생하는 이슈가 있었던걸로 알고 있었는데, 지금은 해소가 된 것일까?
남은 두가지 불편함은 여전하지만, 사소한 수준이기 때문에 무시해도 될 정도라고 인것 같아, 예제는 이쯤에서 마무리한다.

마무리
----

구글이 코틀린에 손을 잡아주면서, 안드로이드 진영에서는 앞으로 더 많은 관심을 받지 않을까?
JVM 서버 진영에서는 스프링이 내민 손을 커뮤니티가 잡아주기를 기대해 본다.


---


> 아래는 2016년 2월에 작성했던 내용이다.


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

애플리케이션이 완전히 실행된 후 브라우저에서 `http://localhost:8080/swagger/ui.html` 페이지에 접속해 API를 테스트해보면 된다.

좋았던 점
-------

- 배우고, 시작하기가 쉬운 언어다. (Java와 호환성이 정말 좋다)
- [Properties support](https://kotlinlang.org/docs/reference/properties.html#declaring-properties)를 비롯해 타입추론, [Single-Expression function](https://kotlinlang.org/docs/reference/functions.html#single-expression-functions), [Smart Casts](https://kotlinlang.org/docs/reference/typecasts.html) 등으로 코드를 간결하게 작성한다.
- [확장 메소드](https://kotlinlang.org/docs/reference/extensions.html)로 코드 표현력 좋아진다.
- null 처리가 매우 안전하다. - [Null Safety](https://kotlinlang.org/docs/reference/null-safety.html)
- 문자열 내부에서 변수에 접근할 수 있어서 유용했다. [String Interpolation](https://kotlinlang.org/docs/reference/idioms.html#string-interpolation)
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
