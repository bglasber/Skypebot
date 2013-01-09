class Grammar:
    
    # noun contexts
    def pluralize(word):
        word = word.strip()
    
        # irregular cases
        if word.lower() == "goose":
            word = "geese"
    
        elif word.lower() == "mouse":
            word = "mice"
    
        elif word.lower() == "sheep":
            word = "sheep"
    
        elif word.lower() == "moose":
            word = "moose"
    
        elif word.lower() == "man":
            word = "men"
    
        elif word.lower() == "woman":
            word = "women"
    
        elif word.lower() == "person":
            word = "people"

        elif word.lower() == "die":
            word = "dice"
            
        else:
            if word.lower().endswith("fe"):
                word = word[:-2] + "ves"
                
            elif word.lower().endswith("us"):
                word = word[:-2] + "i"
                
            elif word.lower().endswith("y"):
                word = word[:-1] + "ies"
    
            elif word.lower().endswith("s"):
                word = word + "es"
                
            elif word.lower().endswith("f"):
                word = word[:-1] + "ves"
            else:
                word += "s"
            
        return word
