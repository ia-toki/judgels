import { hasSubtasks, isFunctional, isInteractive, isOutputOnly } from '../../../../modules/api/gradingEngine';

// The fewest subtasks the form offers: as checkboxes to assign, and as rows of points.
export const MIN_SUBTASK_COUNT = 12;

const SAMPLE_ID = 0;
const NO_SUBTASK_ID = -1;

// What a grading engine's config holds, and so what its form shows.
export function getGradingConfigFields(engine) {
  return {
    limits: !isOutputOnly(engine),
    sourceFileFieldKeys: isFunctional(engine),
    output: !isInteractive(engine),
    subtasks: hasSubtasks(engine),
    customScorer: !isInteractive(engine),
    communicator: isInteractive(engine),
  };
}

// Test data is a list of test groups, of which the first holds the sample test cases.
// An engine without subtasks has one more group, holding every other test case.
// An engine with subtasks has any number more, each assigned to subtasks as a whole,
// while each of its sample test cases is assigned on its own.
export function toFormValues(engine, config) {
  const fields = getGradingConfigFields(engine);
  const [sampleTestGroup, ...testGroups] = config.testData || [];

  const values = {
    sampleTestCases: (sampleTestGroup?.testCases || []).map(testCase => ({
      input: testCase.input,
      output: testCase.output,
      subtaskIds: testCase.subtaskIds.filter(id => id > 0),
    })),
    testGroups: testGroups.map(testGroup => ({
      testCases: testGroup.testCases.map(({ input, output }) => ({ input, output })),
      subtaskIds: (testGroup.testCases[0]?.subtaskIds || []).filter(id => id > 0),
    })),
  };

  if (!fields.subtasks && values.testGroups.length === 0) {
    values.testGroups = [{ testCases: [], subtaskIds: [] }];
  }
  if (fields.limits) {
    values.timeLimit = '' + config.timeLimit;
    values.memoryLimit = '' + config.memoryLimit;
  }
  if (fields.sourceFileFieldKeys) {
    values.sourceFileFieldKeys = (config.sourceFileFieldKeys || []).join(',');
  }
  if (fields.subtasks) {
    const subtaskPoints = (config.subtaskPoints || []).map(points => '' + points);
    while (subtaskPoints.length < MIN_SUBTASK_COUNT) {
      subtaskPoints.push('');
    }
    values.subtaskPoints = subtaskPoints;
  }
  if (fields.customScorer) {
    values.customScorer = config.customScorer || '';
  }
  if (fields.communicator) {
    values.communicator = config.communicator || '';
  }
  return values;
}

function sorted(ids) {
  return [...ids].sort((a, b) => a - b);
}

// Builds on the config the form was made from, to keep what the form does not show.
export function toConfig(engine, config, values) {
  const fields = getGradingConfigFields(engine);
  const newConfig = { ...config };

  const toTestCase = ({ input, output }, subtaskIds) => ({
    input,
    output: fields.output ? output : '',
    subtaskIds,
  });

  if (fields.subtasks) {
    // A test group with no test cases has nothing to assign its subtasks to, so it is dropped.
    const testGroups = values.testGroups.filter(testGroup => testGroup.testCases.length > 0);

    newConfig.testData = [
      {
        id: SAMPLE_ID,
        testCases: values.sampleTestCases.map(testCase =>
          toTestCase(testCase, [SAMPLE_ID, ...sorted(testCase.subtaskIds)])
        ),
      },
      ...testGroups.map((testGroup, idx) => ({
        id: idx + 1,
        testCases: testGroup.testCases.map(testCase => toTestCase(testCase, sorted(testGroup.subtaskIds))),
      })),
    ];

    // There are as many subtasks as the last one that is assigned or given points.
    const subtaskCount = Math.max(
      0,
      ...values.sampleTestCases.flatMap(testCase => testCase.subtaskIds),
      ...testGroups.flatMap(testGroup => testGroup.subtaskIds),
      ...values.subtaskPoints.map((points, idx) => (points === '' ? 0 : idx + 1))
    );
    newConfig.subtaskPoints = values.subtaskPoints.slice(0, subtaskCount).map(points => +points);
  } else {
    newConfig.testData = [
      {
        id: SAMPLE_ID,
        testCases: values.sampleTestCases.map(testCase => toTestCase(testCase, [SAMPLE_ID])),
      },
      {
        id: NO_SUBTASK_ID,
        testCases: values.testGroups[0].testCases.map(testCase => toTestCase(testCase, [NO_SUBTASK_ID])),
      },
    ];
  }

  if (fields.limits) {
    newConfig.timeLimit = +values.timeLimit;
    newConfig.memoryLimit = +values.memoryLimit;
  }
  if (fields.sourceFileFieldKeys) {
    newConfig.sourceFileFieldKeys = values.sourceFileFieldKeys
      .split(',')
      .map(key => key.trim())
      .filter(key => key);
  }
  if (fields.customScorer) {
    newConfig.customScorer = values.customScorer || undefined;
  }
  if (fields.communicator) {
    newConfig.communicator = values.communicator || undefined;
  }
  return newConfig;
}

export function validate(engine, values) {
  const fields = getGradingConfigFields(engine);
  const isPositiveInteger = value => /^[1-9]\d*$/.test(value);

  if (fields.limits && !isPositiveInteger(values.timeLimit)) {
    return 'Time limit must be a positive integer.';
  }
  if (fields.limits && !isPositiveInteger(values.memoryLimit)) {
    return 'Memory limit must be a positive integer.';
  }
  if (fields.sourceFileFieldKeys && !/^\s*\w+\s*(,\s*\w+\s*)*$/.test(values.sourceFileFieldKeys)) {
    return 'Source file keys must be comma-separated words.';
  }
  if (fields.subtasks && values.subtaskPoints.some(points => points !== '' && !/^\d+$/.test(points))) {
    return 'Subtask points must be non-negative integers.';
  }
  return undefined;
}
