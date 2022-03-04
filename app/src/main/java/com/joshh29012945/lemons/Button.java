package com.joshh29012945.lemons;

public class Button extends Object {
    Object linked_object;

    public Button(int x, int y, Object linked_object) {
        super(x, y, 32, 16);
        this.linked_object = linked_object;
        tag = Tag.BUTTON;
    }

    @Override
    public void Update() {

    }

    @Override
    public void OnDeath() {

    }

    @Override
    public void OnCreate() {

    }

    @Override
    public void OnCollide(MasterClass masterClass) {
        Lemon lemon = (Lemon) masterClass;
        linked_object.OnButtonPressed();
    }

    @Override
    protected void OnButtonPressed() {

    }
}
