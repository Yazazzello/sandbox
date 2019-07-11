package by.yazazzello.forum.client.injection;

import javax.inject.Scope;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Scope
@Retention(RetentionPolicy.RUNTIME)
public @interface PerActivity {
}
