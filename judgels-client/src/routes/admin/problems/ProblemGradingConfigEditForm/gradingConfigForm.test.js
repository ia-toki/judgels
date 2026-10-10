import { toConfig, toFormValues, validate } from './gradingConfigForm';

describe('gradingConfigForm', () => {
  const emptyPoints = count => Array(count).fill('');

  test('converts a default config without subtasks', () => {
    const config = {
      timeLimit: 2000,
      memoryLimit: 65536,
      testData: [
        { id: 0, testCases: [] },
        { id: -1, testCases: [] },
      ],
    };

    const values = toFormValues('Batch', config);
    expect(values).toEqual({
      timeLimit: '2000',
      memoryLimit: '65536',
      sampleTestCases: [],
      testGroups: [{ testCases: [], subtaskIds: [] }],
      customScorer: '',
    });
    expect(toConfig('Batch', config, values)).toEqual(config);
  });

  test('converts a default config with subtasks', () => {
    const config = { timeLimit: 2000, memoryLimit: 65536, testData: [{ id: 0, testCases: [] }], subtaskPoints: [] };

    const values = toFormValues('BatchWithSubtasks', config);
    expect(values).toEqual({
      timeLimit: '2000',
      memoryLimit: '65536',
      sampleTestCases: [],
      testGroups: [],
      subtaskPoints: emptyPoints(12),
      customScorer: '',
    });
    expect(toConfig('BatchWithSubtasks', config, values)).toEqual(config);
  });

  test('converts a config with subtasks', () => {
    const config = {
      timeLimit: 2000,
      memoryLimit: 65536,
      testData: [
        {
          id: 0,
          testCases: [
            { input: 'sample_1.in', output: 'sample_1.out', subtaskIds: [0, 2] },
            { input: 'sample_2.in', output: 'sample_2.out', subtaskIds: [0, 1, 2] },
          ],
        },
        {
          id: 1,
          testCases: [
            { input: '1_1.in', output: '1_1.out', subtaskIds: [1, 2] },
            { input: '1_2.in', output: '1_2.out', subtaskIds: [1, 2] },
          ],
        },
        { id: 2, testCases: [{ input: '2_1.in', output: '2_1.out', subtaskIds: [2] }] },
      ],
      subtaskPoints: [30, 70],
      customScorer: 'scorer.cpp',
    };

    const values = toFormValues('BatchWithSubtasks', config);
    expect(values).toEqual({
      timeLimit: '2000',
      memoryLimit: '65536',
      sampleTestCases: [
        { input: 'sample_1.in', output: 'sample_1.out', subtaskIds: [2] },
        { input: 'sample_2.in', output: 'sample_2.out', subtaskIds: [1, 2] },
      ],
      testGroups: [
        {
          testCases: [
            { input: '1_1.in', output: '1_1.out' },
            { input: '1_2.in', output: '1_2.out' },
          ],
          subtaskIds: [1, 2],
        },
        { testCases: [{ input: '2_1.in', output: '2_1.out' }], subtaskIds: [2] },
      ],
      subtaskPoints: ['30', '70', ...emptyPoints(10)],
      customScorer: 'scorer.cpp',
    });
    expect(toConfig('BatchWithSubtasks', config, values)).toEqual(config);
  });

  test('counts a subtask that is assigned but has no points', () => {
    const config = { timeLimit: 2000, memoryLimit: 65536, testData: [{ id: 0, testCases: [] }], subtaskPoints: [] };
    const values = {
      ...toFormValues('BatchWithSubtasks', config),
      testGroups: [{ testCases: [{ input: '1.in', output: '1.out' }], subtaskIds: [3, 1] }],
      subtaskPoints: ['40', ...emptyPoints(11)],
    };

    const newConfig = toConfig('BatchWithSubtasks', config, values);
    expect(newConfig.testData[1]).toEqual({
      id: 1,
      testCases: [{ input: '1.in', output: '1.out', subtaskIds: [1, 3] }],
    });
    expect(newConfig.subtaskPoints).toEqual([40, 0, 0]);
  });

  test('converts an interactive config, which has no outputs', () => {
    const config = {
      timeLimit: 2000,
      memoryLimit: 65536,
      testData: [
        { id: 0, testCases: [{ input: 'sample_1.in', output: '', subtaskIds: [0] }] },
        { id: -1, testCases: [{ input: '1.in', output: '', subtaskIds: [-1] }] },
      ],
      communicator: 'communicator.cpp',
    };

    const values = toFormValues('Interactive', config);
    expect(values.communicator).toEqual('communicator.cpp');
    expect(values.customScorer).toBeUndefined();
    expect(toConfig('Interactive', config, values)).toEqual(config);
  });

  test('converts an output-only config, which has no limits', () => {
    const config = {
      timeLimit: 0,
      memoryLimit: 0,
      testData: [
        { id: 0, testCases: [] },
        { id: -1, testCases: [{ input: '1.in', output: '1.out', subtaskIds: [-1] }] },
      ],
    };

    const values = toFormValues('OutputOnly', config);
    expect(values.timeLimit).toBeUndefined();
    expect(toConfig('OutputOnly', config, values)).toEqual(config);
  });

  test('converts the source file keys of a functional config', () => {
    const config = {
      timeLimit: 2000,
      memoryLimit: 65536,
      sourceFileFieldKeys: ['encoder', 'decoder'],
      testData: [
        { id: 0, testCases: [] },
        { id: -1, testCases: [] },
      ],
    };

    const values = toFormValues('Functional', config);
    expect(values.sourceFileFieldKeys).toEqual('encoder,decoder');
    expect(toConfig('Functional', config, { ...values, sourceFileFieldKeys: 'encoder, decoder' })).toEqual(config);
  });

  test('keeps what the form does not show', () => {
    const config = {
      timeLimit: 2000,
      memoryLimit: 65536,
      testData: [
        { id: 0, testCases: [] },
        { id: -1, testCases: [] },
      ],
      scoringConfig: { roundingMode: 'FLOOR' },
    };

    expect(toConfig('Batch', config, toFormValues('Batch', config)).scoringConfig).toEqual({ roundingMode: 'FLOOR' });
  });

  test('validates', () => {
    const values = toFormValues('FunctionalWithSubtasks', {
      timeLimit: 2000,
      memoryLimit: 65536,
      sourceFileFieldKeys: ['source'],
      testData: [],
      subtaskPoints: [100],
    });

    expect(validate('FunctionalWithSubtasks', values)).toBeUndefined();
    expect(validate('FunctionalWithSubtasks', { ...values, timeLimit: '' })).toMatch(/time limit/i);
    expect(validate('FunctionalWithSubtasks', { ...values, memoryLimit: '-1' })).toMatch(/memory limit/i);
    expect(validate('FunctionalWithSubtasks', { ...values, sourceFileFieldKeys: '' })).toMatch(/source file keys/i);
    expect(validate('FunctionalWithSubtasks', { ...values, subtaskPoints: ['x'] })).toMatch(/subtask points/i);
  });
});
