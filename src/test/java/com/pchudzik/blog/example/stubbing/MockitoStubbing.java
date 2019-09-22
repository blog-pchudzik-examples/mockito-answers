package com.pchudzik.blog.example.stubbing;

import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.exceptions.verification.SmartNullPointerException;

import java.math.BigDecimal;
import java.util.List;

import static com.googlecode.catchexception.CatchException.catchException;
import static com.googlecode.catchexception.CatchException.caughtException;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class MockitoStubbing {
    @Test
    public void returns_defaults() {
        //given
        class SomeObject {
            public int getInt() {
                return 100;
            }

            public boolean getBoolean() {
                return true;
            }

            public String getString() {
                return "abc";
            }

            public List<String> getList() {
                return null;
            }
        }
        final SomeObject object = Mockito.mock(SomeObject.class);

        assertThat(object.getInt(), is(0));
        assertThat(object.getBoolean(), is(false));
        assertThat(object.getString(), nullValue());
        assertThat(object.getList().size(), is(0));
    }

    @Test
    public void calls_real_methods() {
        //given
        class Dragon {
            public String burn() {
                return "fire";
            }

            public int heat() {
                return Integer.MAX_VALUE;
            }
        }
        final Dragon dragonSpy = Mockito.mock(Dragon.class, Mockito.CALLS_REAL_METHODS);

        //expect
        assertThat(dragonSpy.burn(), equalTo("fire"));
        assertThat(dragonSpy.heat(), equalTo(Integer.MAX_VALUE));

        //when
        Mockito.when(dragonSpy.burn()).thenReturn("ice");
        Mockito.when(dragonSpy.heat()).thenReturn(Integer.MIN_VALUE);

        //then
        assertThat(dragonSpy.burn(), is("ice"));
        assertThat(dragonSpy.heat(), is(Integer.MIN_VALUE));
    }

    @Test
    public void returns_smart_nulls() {
        //given
        class SomeObject {
            BigDecimal someValue() {
                return BigDecimal.ZERO;
            }
        }
        final SomeObject object = Mockito.mock(SomeObject.class, Mockito.RETURNS_SMART_NULLS);

        //when
        catchException(object.someValue()).abs();

        //then
        assertThat(caughtException(), isA(SmartNullPointerException.class));
    }

    @Test
    public void returns_self() {
        //given
        class Builder {
            Builder setA() {
                return this;
            }

            Builder setB() {
                return this;
            }

            Builder setC() {
                return this;
            }

            String build() {
                return "done";
            }
        }
        final Builder builder = Mockito.mock(Builder.class, Mockito.RETURNS_SELF);
        Mockito.when(builder.build()).thenReturn("mock");

        //when
        final String result = builder
                .setA()
                .setB()
                .setC()
                .build();

        //then
        assertThat(result, is("mock"));
    }

    @Test
    public void returns_mocks() {
        class Child {
        }
        class Parent {
            Child getChild() {
                return null;
            }
        }
        final Parent parent = Mockito.mock(Parent.class, Mockito.RETURNS_MOCKS);

        //when
        Child child1 = parent.getChild();
        Child child2 = parent.getChild();
        Child child3 = parent.getChild();

        //then
        assertThat(child1, not(is(child2)));
        assertThat(child1, not(is(child3)));
        assertThat(child2, not(is(child3)));
    }

    @Test
    public void returns_deep_stubs() {
        //given
        class Spell {
            String doMagic() {
                return "poof";
            }
        }

        class Wand {
            private Spell spell;

            Spell getSpell() {
                return spell;
            }
        }

        class Fairy {
            private Wand wand;

            Wand getWand() {
                return wand;
            }
        }

        final Fairy deadFairy = Mockito.mock(Fairy.class, Mockito.RETURNS_DEEP_STUBS);

        //when
        Mockito.when(deadFairy.getWand().getSpell().doMagic()).thenReturn("Fairy is dead");

        //then
        assertThat(deadFairy.getWand().getSpell().doMagic(), is("Fairy is dead"));
    }
}
