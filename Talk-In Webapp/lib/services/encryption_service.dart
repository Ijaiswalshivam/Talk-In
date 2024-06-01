String vigenereEncrypt(String text, String keyword) {
  String fullKeyword = _generateFullKeyword(text, keyword);
  return String.fromCharCodes(
      text.codeUnits.asMap().entries.map((entry) {
        int idx = entry.key;
        int char = entry.value;
        int shift = _getShift(fullKeyword[idx] as int);

        if (char >= 65 && char <= 90) {
          return 65 + (char - 65 + shift) % 26;
        } else if (char >= 97 && char <= 122) {
          return 97 + (char - 97 + shift) % 26;
        } else {
          return char;
        }
      }).toList()
  );
}

String vigenereDecrypt(String text, String keyword) {
  String fullKeyword = _generateFullKeyword(text, keyword);
  return String.fromCharCodes(
      text.codeUnits.asMap().entries.map((entry) {
        int idx = entry.key;
        int char = entry.value;
        int shift = _getShift(fullKeyword[idx] as int);

        if (char >= 65 && char <= 90) {
          return 65 + (char - 65 - shift + 26) % 26;
        } else if (char >= 97 && char <= 122) {
          return 97 + (char - 97 - shift + 26) % 26;
        } else {
          return char;
        }
      }).toList()
  );
}

String _generateFullKeyword(String text, String keyword) {
  int textLength = text.length;
  int keywordLength = keyword.length;
  StringBuffer fullKeyword = StringBuffer();

  for (int i = 0, j = 0; i < textLength; i++) {
    if (text.codeUnitAt(i) >= 65 && text.codeUnitAt(i) <= 90 ||
        text.codeUnitAt(i) >= 97 && text.codeUnitAt(i) <= 122) {
      fullKeyword.write(keyword[j % keywordLength]);
      j++;
    } else {
      fullKeyword.write(' ');
    }
  }
  return fullKeyword.toString();
}

int _getShift(int charCode) {
  if (charCode >= 65 && charCode <= 90) {
    return charCode - 65;
  } else if (charCode >= 97 && charCode <= 122) {
    return charCode - 97;
  } else {
    return 0;
  }
}