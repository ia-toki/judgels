import { webConfigActions } from './webConfigActions';
import { PutWebConfig } from './webConfigReducer';
import { WebConfig } from '../../../modules/api/jophiel/web';

describe('webConfigActions', () => {
  let dispatch: jest.Mock<any>;
  let getState: jest.Mock<any>;

  let webAPI: jest.Mocked<any>;

  beforeEach(() => {
    dispatch = jest.fn();
    getState = jest.fn();

    webAPI = {
      getConfig: jest.fn(),
    };
  });

  describe('get()', () => {
    const { get } = webConfigActions;
    const doGet = async () => get()(dispatch, getState, { webAPI });

    const webConfig: WebConfig = {
      userRegistration: {
        useRecaptcha: true,
      },
    };

    beforeEach(async () => {
      webAPI.getConfig.mockImplementation(() => webConfig);

      await doGet();
    });

    it('calls API to get web config', () => {
      expect(webAPI.getConfig).toHaveBeenCalled();
    });

    it('puts the web config', () => {
      expect(dispatch).toHaveBeenCalledWith(PutWebConfig.create(webConfig));
    });
  });
});
