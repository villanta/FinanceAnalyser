package com.financeanalyser.view.components.controls;

import java.util.Optional;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class FullScreenPane extends AnchorPane {

	private BooleanProperty fullscreen = new SimpleBooleanProperty(false);

	private Parent realSceneRoot;

	private AnchorPane stretchyPane;

	public FullScreenPane() {
		super();
		stretchyPane = new AnchorPane();
		setZeroAnchors(stretchyPane);
		super.getChildren().add(stretchyPane);
	}

	public void setFramedNode(Node framedNode) {
		setZeroAnchors(framedNode);
		stretchyPane.getChildren().add(framedNode);

		Button fullscreenToggleButton = new Button("+ / -");
		fullscreenToggleButton.setOnAction(this::toggle);
		AnchorPane.setTopAnchor(fullscreenToggleButton, 10.0);
		AnchorPane.setRightAnchor(fullscreenToggleButton, 10.0);
		stretchyPane.getChildren().add(fullscreenToggleButton);
	}

	public void removeFramedNode() {
		stretchyPane.getChildren().clear();
	}

	public BooleanProperty fullscreenProperty() {
		return this.fullscreen;
	}

	public boolean isFullscreen() {
		return this.fullscreenProperty().get();
	}

	public void setFullscreen(final boolean fullscreen) {
		this.fullscreenProperty().set(fullscreen);
	}

	private void toggle(ActionEvent event) {
		if (isFullscreen()) {
			closeFullscreen();
		} else {
			goFullscreen();
		}

		event.consume();
	}

	private void closeFullscreen() {
		setFullscreen(false);

		Optional<Scene> thisScene = getThisScene();
		if (thisScene.isPresent()) {
			thisScene.get().setRoot(realSceneRoot);
			this.getChildren().add(stretchyPane);
		}
	}

	private void goFullscreen() {
		setFullscreen(true);

		Optional<Scene> thisScene = getThisScene();
		if (thisScene.isPresent()) {
			realSceneRoot = thisScene.get().getRoot();
			thisScene.get().setRoot(generateFullscreenRoot(thisScene.get()));
		}
	}

	private Parent generateFullscreenRoot(Scene scene) {
		AnchorPane anchorPane = new AnchorPane(stretchyPane);

		anchorPane.setPrefSize(scene.getWidth(), scene.getHeight());
		return anchorPane;
	}

	private Optional<Scene> getThisScene() {
		return Optional.ofNullable(stretchyPane.getScene());
	}

	private void setZeroAnchors(Node n) {
		AnchorPane.setBottomAnchor(n, 0.0);
		AnchorPane.setTopAnchor(n, 0.0);
		AnchorPane.setLeftAnchor(n, 0.0);
		AnchorPane.setRightAnchor(n, 0.0);
	}

}
