# from https://github.com/jakublipinski/export-gsheet-to-app-resources
import gspread
from oauth2client.service_account import ServiceAccountCredentials
import argparse
from pathlib import Path, PurePath
from lxml import etree

parser = argparse.ArgumentParser(description='Export Google Sheet to app resource files')
parser.add_argument('--credentials', type=str, required=True, help=".json credential file downloaded from Google Developer Console")
parser.add_argument('--gsheet_key', type=str, required=True, help="Google Sheet Key (copied from the sheet url)")
parser.add_argument('--android_res', type=str, help="Path to the Android res folder")
parser.add_argument('--ios_res', type=str, help="Path to the iOS res folder")
parser.add_argument
args = parser.parse_args()

scope = ['https://spreadsheets.google.com/feeds',
        'https://www.googleapis.com/auth/drive']
credentials = ServiceAccountCredentials.from_json_keyfile_name(args.credentials, scope)
gc = gspread.authorize(credentials)
doc = gc.open_by_key(args.gsheet_key)
for s, sheet in enumerate(doc.worksheets()):
    columns = sheet.row_values(1)
    if columns[0] != "ID":
        print(f"First column in {sheet.title} should be 'ID', found '{columns[0]}'. Exiting.")
        exit(1)
    if columns[-1] != "Comment":
        print(f"Last column in {sheet.title} should be 'Comment', found '{columns[-1]}'. Exiting.")
        exit(1)

    for i, lang in enumerate(columns[1:-1]):
        ids, comments = sheet.col_values(1), sheet.col_values(len(columns))
        texts = sheet.col_values(i+2)
        if args.android_res:
            values = "values" if i==0 else f"values-{lang}"
            res_path = PurePath(args.android_res).joinpath(values)
            Path(res_path).mkdir(parents=True, exist_ok=True)

            resources = etree.Element("resources")
            for j in range(1,len(texts)):
                resources.append(etree.Comment(comments[j]))
                elem = etree.SubElement(resources, "string", name=ids[j])
                elem.text = texts[j]

            tree = etree.ElementTree(resources)
            res_filename = res_path.joinpath(f"{sheet.title}.xml")
            print(f"Writing {lang} to {res_filename} from sheet {sheet.title}...")
            tree.write(str(res_filename), encoding="utf-8", xml_declaration=True, pretty_print=True)

        if args.ios_res:
            res_path = PurePath(args.ios_res).joinpath(f"{lang}.lproj")
            Path(res_path).mkdir(parents=True, exist_ok=True)
            res_filename = res_path.joinpath(f"Localizable.strings")
            print(f"Writing {lang} to {res_filename} from sheet {sheet.title}...")

            with open(res_filename, 'w' if s == 0 else 'a') as strings:
                for j in range(1,len(texts)):
                    strings.write(f'/* {comments[j]} */\n')
                    strings.write(f'"{ids[j]}" = "{texts[j]}";\n')
                    strings.write("\n")



