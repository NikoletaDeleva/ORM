package com.egtinteractive.orm.classes;

import com.egtinteractive.orm.annotations.Column;

public class Person {
    @Column(name = "egn")
    private long egn = 0;
    @Column(name = "height")
    final int hight = 0;
    @Column(name = "weight")
    int weight = 0;

    public Person() {

    }

}
