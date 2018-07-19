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
      getWebConfig: jest.fn(),
    };
  });

  describe('getWebConfig()', () => {
    const { getWebConfig } = webConfigActions;
    const doGetWebConfig = async () => getWebConfig()(dispatch, getState, { webAPI });

    const webConfig: WebConfig = {
      userRegistration: {
        useRecaptcha: true,
      },
    };

    beforeEach(async () => {
      webAPI.getWebConfig.mockImplementation(() => webConfig);

      await doGetWebConfig();
    });

    it('calls API to get web config', () => {
      expect(webAPI.getWebConfig).toHaveBeenCalled();
    });

    it('puts the web config', () => {
      expect(dispatch).toHaveBeenCalledWith(PutWebConfig.create(webConfig));
    });
  });
});
