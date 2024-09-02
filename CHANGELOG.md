# Dev Changelog — Electric Hiccup

## :git/tag "v0.1.0-alpha"

* Initial release

## :git/tag "v0.1.1-alpha" :git/sha "28573b8"

Enhancements:

* Support single keyword class-name as property #1

## :git/tag "v0.2.0-alpha" :git/sha "537ec9d"

Enhancements:

* Support property :class values in the form :my-class1.my-class2 #2
* Support runtime expressions as class property value #4

## :git/tag "v0.2.1-alpha" :git/sha "78fee86"

Enhancements:

* Support classes as a list as well as a vector [from class functions]. #5

Bugs:

* The ID defined in the properties should take precedence over that on the tag #6

## :git/tag "v0.3.0-alpha" :git/sha "650c9e3"

Enhancements:

* Add support for [:.h-1] as supported by hiccup #7
* Allow the package dependency on electric to be configured. #8

## :git/tag "v0.4.0-alpha" :git/sha TBD

* Validate hiccup tag form for correct use of "#" and "." #12
* Improve message for invalid hiccup tag #11
* Add support for arbitrary electric namespaces #13