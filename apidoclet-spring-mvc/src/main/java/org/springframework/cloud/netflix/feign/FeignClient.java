package org.springframework.cloud.netflix.feign;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignClient {

    /**
     * The serviceId with optional protocol prefix. Synonym for {@link #serviceId()
     * serviceId}. Either serviceId or url must be specified but not both. Can be
     * specified as property key, eg: ${propertyKey}.
     */
    String value() default "";

    /**
     * The serviceId with optional protocol prefix. Synonym for {@link #value() value}.
     * Either serviceId or url must be specified but not both. Can be
     * specified as property key, eg: ${propertyKey}.
     *
     * @deprecated use {@link #name() name} instead
     */
    @Deprecated
    String serviceId() default "";

    String name() default "";

    /**
     * An absolute URL or resolvable hostname (the protocol is optional).
     */
    String url() default "";

    /**
     * Whether 404s should be decoded instead of throwing FeignExceptions
     */
    boolean decode404() default false;

    /**
     * A custom <code>@Configuration</code> for the feign client. Can contain override
     * <code>@Bean</code> definition for the pieces that make up the client, for instance
     * {@link feign.codec.Decoder}, {@link feign.codec.Encoder}, {@link feign.Contract}.
     *
     * @see FeignClientsConfiguration for the defaults
     */
    Class<?>[] configuration() default {};
}