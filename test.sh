echo "{\"name\":\"Lint Check\",\"head_sha\":\"${GITHUB_SHA}\",\"conclusion\":\"success\"}"
echo "token=[$0]"
curl -X POST -H "Accept: application/vnd.github.v3+json" -H "Authorization: Bearer $0" https://api.github.com/repos/SYtor/Kanji-Dojo/check-runs -d "{\"name\":\"Lint Check\",\"head_sha\":\"${GITHUB_SHA}\",\"conclusion\":\"success\"}"