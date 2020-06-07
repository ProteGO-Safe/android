# Downloading Diagnosis Keys

A regularly scheduled job is triggered to download files with batch of Temporary Exposure Keys (**TEK**) of positively diagnosed, called Diagnosis Keys files. Each of the batch file has unique timestamp. The timestamp that identifying when a certain Diagnosis Keys file was created is used to select only not yet analyzed files for download.

Steps:
- The application gets list of available Diagnosis Keys files from CDN
- Only files with the timestamp older than the latest successfully provided to analyze batch are selected for download
- Files are downloaded over HTTPS protocol to internal device storage

After the above steps Diagnosis Keys files are ready to provide it for exposure checking process.
