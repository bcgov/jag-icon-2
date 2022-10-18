## Templates to create openshift components related to jag-icon2-common-1-2 api deployment

### Command to execute template
1) Login to OC using login command
2) Run below command in each env. namespace dev/test/prod/tools
   ``oc process -f jag-icon2-common-1-2.yaml --param-file=jag-icon2-common-1-2.env | oc apply -f -``
