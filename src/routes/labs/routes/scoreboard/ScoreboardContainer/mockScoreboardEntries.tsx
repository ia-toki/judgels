var Papa = require('./papaparse.min.js');
var content = `#,Country Code,Username,Total,A1,A2,B1,B2,C1,C2,D1,D2,E1,E2,F1,F2
1,ID,johndoe,12/488/plain,1/3/first-accepted,1/3/first-accepted,1/21/first-accepted,2/42/accepted,1/33/accepted,1/56/accepted,1/110/accepted,1/122/accepted,1/80/accepted,1/86/first-accepted,1/43/first-accepted,1/45/first-accepted
2,MY,limewash,12/732/plain,1/3/accepted,1/3/accepted,1/21/accepted,2/42/first-accepted,1/33/accepted,1/56/accepted,1/110/first-accepted,1/122/accepted,1/80/first-accepted,1/86/accepted,1/43/accepted,1/45/accepted
3,ID,andygroove,12/1568/plain,1/3/accepted,1/3/accepted,1/21/accepted,2/42/accepted,1/33/accepted,1/56/accepted,1/110/accepted,1/122/accepted,1/80/accepted,1/86/accepted,1/43/accepted,1/45/accepted
4,SG,sergeybrin,11/1568/plain,1/3/accepted,1/3/accepted,1/21/accepted,2/42/accepted,1/33/accepted,1/56/accepted,1/110/accepted,1/122/failed,1/80/accepted,8/x/failed,1/43/accepted,1/45/accepted
5,ID,johnnash,8/1568/plain,1/3/accepted,1/3/accepted,1/21/accepted,2/42/accepted,1/33/failed,1/56/failed,1/110/accepted,1/122/accepted,1/80/accepted,3/x/failed,1/43/accepted,---/--/plain
6,WW,greentea,8/1568/plain,1/3/accepted,1/3/accepted,1/21/accepted,2/42/accepted,---/--/plain,---/--/plain,1/110/accepted,1/122/accepted,1/80/accepted,---/--/plain,1/43/accepted,---/--/plain`;

import { ScoreboardEntry } from '../../scoreboard/Scoreboard/Scoreboard';
import { ScoreboardElement } from '../../scoreboard/ScoreboardCell/ScoreboardCell';

var heads: string[] = [];
var mockScoreboardEntries: ScoreboardEntry[] = [];
Papa.parse(content, {
  step: function(row) {
    if (row.data[0][0] === '#') {
      row.data[0].forEach(function(value, index) {
        if (index >= 3) {
          heads.push(value);
        }
      });
    } else {
      var elements: ScoreboardElement[] = [];
      row.data[0].forEach(function(value, index) {
        var elementFields = value.split('/');
        if (index >= 3) {
          elements.push({
            key: heads[index - 3],
            strong: elementFields[0],
            small: elementFields[1],
            theme: elementFields[2],
          });
        }
      });
      mockScoreboardEntries.push({
        key: row.data[0][0],
        countryCode: row.data[0][1],
        username: row.data[0][2],
        elements: elements,
      });
    }
  },
});

export default mockScoreboardEntries;
