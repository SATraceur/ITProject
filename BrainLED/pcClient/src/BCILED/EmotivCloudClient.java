/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BCILED;

import java.util.List;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.*;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

/**
 *
 * @author yeqin
 */
public interface EmotivCloudClient extends Library {

    /**
     *
     */
    EmotivCloudClient INSTANCE = (EmotivCloudClient) Native.loadLibrary("edk", EmotivCloudClient.class);

    //! Profile types

    /**
     *
     */
    public enum profileType_t {

        /**
         *
         */
        TRAINING(0),

        /**
         *
         */
        EMOKEY(1);

        private int type;

        profileType_t(int val) {
            type = val;
        }

        /**
         *
         * @return
         */
        public int toInt() {
            return type;
        }
    };

    /**
     *
     */
    public static class profileVerInfo extends Structure {

        int version;
        char[] last_modified;

        /**
         *
         * @return
         */
        @Override
        protected List getFieldOrder() {
            // TODO Auto-generated method stub
            return null;
        }
    }

    //! Initialize the connection to Emotiv Cloud Server
    /*!
     *  \return EDK_ERROR_CODE
     *              - true if connect successfully
     */

    /**
     *
     * @return
     */

    int EC_Connect();

    //! Reconnection to Emotiv engine
    /*!
	 *  \return EDK_ERROR_CODE
	 *              - true if Reconnect successfully
     */

    /**
     *
     * @return
     */

    int EC_ReconnectEngine();

    //! Disconnection to Emotiv engine
    /*!
	 *   \return EDK_ERROR_CODE
	 *              - true if Reconnect successfully
     */

    /**
     *
     * @return
     */

    int EC_DisconnectEngine();

    //! Terminate the connection to Emotiv Cloud server
    /*!
     */

    /**
     *
     * @return
     */

    int EC_Disconnect();

    //! Login Emotiv Cloud with EmotivID
    /*!
     *  To register a new EmotivID please visit https://id.emotivcloud.com/ .
     *  \param username  - username
     *  \param password  - password
     *  \return EDK_ERROR_CODE
     *              - true if login successfully
     */

    /**
     *
     * @param username
     * @param password
     * @return
     */

    int EC_Login(String username, String password);

    //! Logout Emotiv Cloud
    /*
     *  \return EDK_ERROR_CODE
     *              - true if logout successfully
     */

    /**
     *
     * @param userCloudID
     * @return
     */

    int EC_Logout(int userCloudID);

    //! Get user ID after login
    /*!
     *  \param userCloudID - return user ID for subsequence requests
     *  \return EDK_ERROR_CODE
     *              - true if fetched successfully
     */

    /**
     *
     * @param userCloudID
     * @return
     */

    int EC_GetUserDetail(IntByReference userCloudID);

    //! Save user profile to Emotiv Cloud
    /*!
     *  \param userCloudID  - user ID from EC_GetUserDetail()
     *  \param engineUserID - user ID from current EmoEngine (first user will be 0)
     *  \param profileName  - profile name to be saved as
     *  \param ptype        - profile type
     *  \return EDK_ERROR_CODE
     *              - true if saved successfully
     */

    /**
     *
     * @param userCloudID
     * @param engineUserID
     * @param profileName
     * @param ptype
     * @return
     */

    int EC_SaveUserProfile(int userCloudID, int engineUserID, String profileName, int ptype);

    //! Update user profile to Emotiv Cloud
    /*!
     *  \param userCloudID  - user ID from EC_GetUserDetail()
     *  \param engineUserID - user ID from current EmoEngine (first user will be 0)
     *  \param profileId    - profile ID to be updated, from EC_GetProfileId()
     *  \param profileName  - profile name to be saved as
     *  \return EDK_ERROR_CODE 
     *               - true if updated successfully
     */

    /**
     *
     * @param userCloudID
     * @param engineUserID
     * @param profileId
     * @return
     */

    int EC_UpdateUserProfile(int userCloudID, int engineUserID, int profileId);

    //! Delete user profile from Emotiv Cloud
    /*!
     *  \param userCloudID  - user ID from EC_GetUserDetail()
     *  \param profileId    - profile ID to be deleted, from EC_GetProfileId()
     *  \return EDK_ERROR_CODE
     *                - true if updated successfully
     */

    /**
     *
     * @param userCloudID
     * @param profileId
     * @return
     */

    int EC_DeleteUserProfile(int userCloudID, int profileId);

    //! Get profile ID of a user
    /*!
     *  \param userCloudID  - user ID from EC_GetUserDetail()
     *  \param profileName  - profile name to look for
     *  \param profileID    - profile id with name
     *  \return EDK_ERROR_CODE
     */

    /**
     *
     * @param userCloudID
     * @param profileName
     * @param profileID
     * @return
     */

    int EC_GetProfileId(int userCloudID, String profileName, IntByReference profileID);

    //! Load profile from Emotiv Cloud
    /*!
     *  \remark Time to take to load a profile from Emotiv Cloud depends on network speed and profile size.
     *  \param userCloudID  - user ID from EC_GetUserDetail()
     *  \param engineUserID - user ID from current EmoEngine (first user will be 0)
     *  \param profileId    - profile ID to be loaded, from EC_GetProfileId()
     *  \param version      - version of profile to download (default: -1 for lastest version)
     *  \return EDK_ERROR_CODE
     *               - true if loaded successfully
     */

    /**
     *
     * @param userCloudID
     * @param engineUserID
     * @param profileId
     * @param version
     * @return
     */

    int EC_LoadUserProfile(int userCloudID, int engineUserID, int profileId, int version);

    //! Update all the profile info from Emotiv Cloud
    /*!
     *  This function needs to be called first before calling EC_ProfileIDAtIndex(), EC_ProfileNameAtIndex(),
     *  EC_ProfileLastModifiedAtIndex(), EC_ProfileTypeAtIndex()
     *  \param userCloudID  - user ID from EC_GetUserDetail()
     *  \return int 
     *              - number of existing profiles (only latest version for each profile are counted)
     */

    /**
     *
     * @param userCloudID
     * @return
     */

    int EC_GetAllProfileName(int userCloudID);

    //! Return the profile ID of a profile in cache
    /*!
     *  \param userCloudID  - user ID from EC_GetUserDetail()
     *  \param index        - index of profile (starts from 0)
     *  \return int 
     *               - profile ID
     */

    /**
     *
     * @param userCloudID
     * @param index
     * @return
     */

    int EC_ProfileIDAtIndex(int userCloudID, int index);

    //! Return the profile name of a profile in cache
    /*! \param userCloudID  - user ID from EC_GetUserDetail()
     *  \param index        - index of profile (starts from 0)
     *  \return String 
     *               - profile name
     */

    /**
     *
     * @param userCloudID
     * @param index
     * @return
     */

    String EC_ProfileNameAtIndex(int userCloudID, int index);

    //! Return the last modified timestamp of a profile in cache
    /*!
     * \param userCloudID  - user ID from EC_GetUserDetail()
     * \param index        - index of profile (starts from 0)
     * \return String - last modified timestamp
     */

    /**
     *
     * @param userCloudID
     * @param index
     * @return
     */

    String EC_ProfileLastModifiedAtIndex(int userCloudID, int index);

    //! Return the type of a profile in cache
    /*!
     * \param userCloudID  - user ID from EC_GetUserDetail()
     * \param index        - index of profile (starts from 0)
     * \return profileFileType - profile type
     */

    /**
     *
     * @param userCloudID
     * @param index
     * @return
     */

    int EC_ProfileTypeAtIndex(int userCloudID, int index);

    //! Donwload file Profile
    /*!
     * \param cloudUserID  - id of user
     * \param profileId
     * \param destPath
     * \param version      - default = -1 for download lastest version
     * \return EDK_ERROR_CODE
                        - EDK_OK if success
     */

    /**
     *
     * @param cloudUserId
     * @param profileId
     * @param destPath
     * @param version
     * @return
     */

    int EC_DownloadProfileFile(int cloudUserId, int profileId,
            String destPath, int version);

    //! Upload file profile of user
    /*!
     * \param cloudUserID   - id of user
     * \param profileName
     * \param filePath
     * \param ptype
     * \return EDK_ERROR_CODE
     *                      - EDK_OK if success
     */

    /**
     *
     * @param cloudUserId
     * @param profileName
     * @param filePath
     * @param ptype
     * @param overwrite_if_exists
     * @return
     */

    int EC_UploadProfileFile(int cloudUserId, String profileName,
            String filePath, int ptype,
            Boolean overwrite_if_exists);

    //! get lastest version of profile
    /*
     * \param profileID    - profileID
     * \param pVersionInfo - receives array of version Informations
     * \param nVersion     - receives number of versions
     * \return EDK_ERROR_CODE
     *                     - EDK_OK if success
     */

    /**
     *
     * @param userID
     * @param profileID
     * @param nVersion
     * @return
     */

    int EC_GetLastestProfileVersions(int userID, int profileID,
            IntByReference nVersion);
}
