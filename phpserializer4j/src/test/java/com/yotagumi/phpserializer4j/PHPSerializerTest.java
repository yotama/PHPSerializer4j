package com.yotagumi.phpserializer4j;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for PHPSerializer
 * 
 * @author Yotama
 * 
 */
public class PHPSerializerTest {
    PHPSerializer target;

    @Before
    public void setUp() throws Exception {
        target = new PHPSerializer();
    }

    @Test
    public void test_serialize_Null() {
        assertThat(target.serialize(null), is("N;"));
    }

    @Test
    public void test_serialize_String() {
        assertThat(target.serialize("Yotama"), is("s:6:\"Yotama\";"));
        assertThat(target.serialize("与太組"), is("s:" + "与太組".getBytes().length + ":\"与太組\";"));
    }

    @Test
    public void test_serialize_Boolean() {
        assertThat(target.serialize(Boolean.valueOf("true")), is("b:1;"));
        assertThat(target.serialize(Boolean.valueOf("false")), is("b:0;"));
    }

    @Test
    public void test_serialize_Integer() {
        assertThat(target.serialize(Integer.valueOf(Integer.MAX_VALUE)), is("i:2147483647;"));
        assertThat(target.serialize(Long.valueOf(Long.MAX_VALUE)), is("i:9223372036854775807;"));
        assertThat(target.serialize(Byte.valueOf(Byte.MAX_VALUE)), is("i:127;"));
        assertThat(target.serialize(Short.valueOf(Short.MAX_VALUE)), is("i:32767;"));
    }

    @Test
    public void test_serialize_Double() {
        assertThat(target.serialize(Double.valueOf(0.01d)), is("d:0.01;"));
        assertThat(target.serialize(Float.valueOf(0.1f)), is("d:0.1;"));
        assertThat(target.serialize(BigDecimal.valueOf(11.9)), is("d:11.9;"));
    }

    @Test
    public void test_serialize_Map() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Yotama");
        map.put("age", 28);
        assertThat(target.serialize(map), is("a:2:{s:3:\"age\";i:28;s:4:\"name\";s:6:\"Yotama\";}"));

        Map<String, String> emptyMap = new HashMap<>();
        assertThat(target.serialize(emptyMap), is("a:0:{}"));
    }

    @Test
    public void test_serialize_List() {
        List<String> list = new ArrayList<>();
        list.add("Kagoshima");
        list.add("Tokyo");
        assertThat(target.serialize(list), is("a:2:{i:0;s:9:\"Kagoshima\";i:1;s:5:\"Tokyo\";}"));

        List<Integer> emptyList = new ArrayList<>();
        assertThat(target.serialize(emptyList),  is("a:0:{}"));
    }

    @Test
    public void test_serialize_array() {
        int[] intArray = {10, 20};
        assertThat(target.serialize(intArray), is("a:2:{i:0;i:10;i:1;i:20;}"));
        long[] longArray = { 11L, 22L };
        assertThat(target.serialize(longArray), is("a:2:{i:0;i:11;i:1;i:22;}"));
        short[] shortArray = { 12, 23 };
        assertThat(target.serialize(shortArray), is("a:2:{i:0;i:12;i:1;i:23;}"));
        byte[] byteArray = { 0, 127 };
        assertThat(target.serialize(byteArray), is("a:2:{i:0;i:0;i:1;i:127;}"));
        double[] doubleArray = { 0.01, 2.1 };
        assertThat(target.serialize(doubleArray), is("a:2:{i:0;d:0.01;i:1;d:2.1;}"));
        float[] floatArray = { 0.02f, 2.2f };
        assertThat(target.serialize(floatArray), is("a:2:{i:0;d:0.02;i:1;d:2.2;}"));
        Object[] objectArray = { Boolean.valueOf(true), "Groovy" };
        assertThat(target.serialize(objectArray), is("a:2:{i:0;b:1;i:1;s:6:\"Groovy\";}"));

        int[] emptyIntArray = new int[0];
        assertThat(target.serialize(emptyIntArray), is("a:0:{}"));
    }

    @Test
    public void test_serialize_bean() {
        class TestBaen {
            private String name;
            private int age;

            @SuppressWarnings("unused")
            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            @SuppressWarnings("unused")
            public int getAge() {
                return age;
            }

            public void setAge(int age) {
                this.age = age;
            }
        }

        TestBaen dto = new TestBaen();
        dto.setName("Yotama");
        dto.setAge(28);

        assertThat(target.serialize(dto), is("a:2:{s:3:\"age\";i:28;s:4:\"name\";s:6:\"Yotama\";}"));
    }
}
