package com.strategames.engine.screens;

import java.util.HashMap;
import java.util.Map;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class GdxTestRunner extends BlockJUnit4ClassRunner implements ApplicationListener {

	private static LwjglApplication application;

	private Map<FrameworkMethod, RunNotifier> invokeInRender = new HashMap<FrameworkMethod, RunNotifier>();

	public GdxTestRunner(Class<?> klass) throws InitializationError {
		super(klass);
		System.out.println("GdxTestRunner constructor called");
		synchronized (this) {
			if( application == null ) {
				LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
				config.title = "core-test";
				config.width = 504;
				config.height = 800;
				application = new LwjglApplication(this, config);
				
			}
		}
	}

	@Override
	public void create() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void render() {
		System.out.println("GdxTestRunner render called");
		synchronized (invokeInRender) {
			System.out.println("GdxTestRunner rendering");
			for(Map.Entry<FrameworkMethod, RunNotifier> each : invokeInRender.entrySet()){
				System.out.println("Running test: "+each.getKey() +" : "+each.getValue());
				super.runChild(each.getKey(), each.getValue());
			}
			invokeInRender.clear();
		}
		System.out.println("GdxTestRunner finished rendering");
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		System.out.println("GdxTestRunner runChild called");
		synchronized (invokeInRender) {
			System.out.println("GdxTestRunner adding method: "+method);
			//add for invoking in render phase, where gl context is available
			invokeInRender.put(method, notifier);   
		}
		//wait until that test was invoked
		waitUntilInvokedInRenderMethod();
		System.out.println("GdxTestRunner runChild finished");
	}

	/**
	 * 
	 */
	private void waitUntilInvokedInRenderMethod() {
		System.out.println("GdxTestRunner waitUntilInvokedInRenderMethod called");
		try {
			while (true){
				Thread.sleep(10);
				synchronized (invokeInRender) {
					System.out.println("GdxTestRunner checking if invokeInRender is empty");
					if (invokeInRender.isEmpty()) break;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
}
