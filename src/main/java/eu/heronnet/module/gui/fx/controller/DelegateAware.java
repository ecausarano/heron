package eu.heronnet.module.gui.fx.controller;

/**
 * @author edoardocausarano
 */
public interface DelegateAware<D> {

    D getDelegate();

    void setDelegate(D delegate);
}
