# APJP, A PHP/JAVA PROXY
# Copyright (C) 2009-2012 Jeroen Van Steirteghem
# 
# This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

module APJP
  class APJP
    @@APJP_KEY = ""
    @@APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY = ["", "", "", "", ""]
    @@APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE = ["", "", "", "", ""]
    @@APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY = ["", "", "", "", ""]
    @@APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_VALUE = ["", "", "", "", ""]
    
    def APJP.APJP_KEY
        return @@APJP_KEY
    end
    
    def APJP.APJP_KEY=(x)
        @@APJP_KEY = x
    end
    
    def APJP.APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY
        return @@APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY
    end
    
    def APJP.APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY=(x)
        @@APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_KEY = x
    end
    
    def APJP.APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE
        return @@APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE
    end
    
    def APJP.APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE=(x)
        @@APJP_REMOTE_HTTP_SERVER_RESPONSE_PROPERTY_VALUE = x
    end
    
    def APJP.APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY
        return @@APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY
    end
    
    def APJP.APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY=(x)
        @@APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY = x
    end
    
    def APJP.APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_VALUE
        return @@APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_VALUE
    end
    
    def APJP.APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_VALUE=(x)
        @@APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_VALUE = x
    end
  end
end