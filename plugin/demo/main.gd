extends Node2D

var _plugin_name = "GodotAndroidPushNotifications"
var _plugin

func _ready():
	if Engine.has_singleton(_plugin_name):
		_plugin = Engine.get_singleton(_plugin_name)
		_plugin.WillSendDeviceToken.connect(_on_token_received)
		_plugin.RegistrationError.connect(_on_registration_error)
	else:
		print("Couldn't find plugin " + _plugin_name)

func _on_register_pressed():
	if _plugin:
		_plugin.registerPushNotifications()

func _on_token_received(token: String):
	print("FCM Token: " + token)
	$Label.text = "Token: " + token

func _on_registration_error(error: String):
	print("Registration failed: " + error)
	$Label.text = "Error: " + error
