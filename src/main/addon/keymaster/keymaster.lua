local UberPwn_version = 1;
local UberPwn_Pixels = {}

local UberPwn_Pixel_Size = 3;
local UberPwn_Rows=3;
local UberPwn_Cols=20;

function UberPwn_OnLoad()
  UberPwn_Initialize();
  UberPwnFrame_Attack_Color:SetVertexColor(0,0,0);
end

function UberPwn_Initialize()
  local cornerFrame = CreateFrame("Frame",nil,UberPwnFrame);
  cornerFrame:SetWidth(UberPwn_Pixel_Size);
  cornerFrame:SetHeight(UberPwn_Pixel_Size);

  local cornerTexture = cornerFrame:CreateTexture();
  cornerTexture:SetColorTexture(51/255,102/255,204/255);
  cornerTexture:SetAllPoints(cornerFrame);
  cornerTexture.texture = cornerTexture;

  cornerFrame:SetPoint("TOPLEFT",0,0);
  cornerFrame:Show();

  for row=1,UberPwn_Rows
  do
    local f = CreateFrame("Frame",nil,UberPwnFrame);
    f:SetWidth(UberPwn_Pixel_Size);
    f:SetHeight(UberPwn_Pixel_Size);

    local t = f:CreateTexture();
    if row == UberPwn_Rows then
      t:SetColorTexture(204/255,102/255,51/255);
    elseif row % 2 == 0 then
      t:SetColorTexture(1,1,247/255);
    else
      t:SetColorTexture(1,1,250/255);
    end
    t:SetAllPoints(f);
    f.texture = t;

    f:SetPoint("TOPLEFT",0,(-row*UberPwn_Pixel_Size));
    f:Show();
  end

  for col=1,UberPwn_Cols
  do
    local f = CreateFrame("Frame",nil,UberPwnFrame);
    f:SetWidth(UberPwn_Pixel_Size);
    f:SetHeight(UberPwn_Pixel_Size);

    local t = f:CreateTexture();
    if col == UberPwn_Cols then
      t:SetColorTexture(204/255,102/255,51/255);
    elseif col % 2 == 0 then
      t:SetColorTexture(1,1,247/255);
    else
      t:SetColorTexture(1,1,250/255);
    end
    t:SetAllPoints(f);
    f.texture = t;

    f:SetPoint("TOPLEFT",(col*UberPwn_Pixel_Size),0);
    f:Show();
  end

  for row=1,UberPwn_Rows
  do
    for col=1,UberPwn_Cols
    do
      if row == 1 and col == 1 then
        UberPwn_Pixels[col+((row-1)*UberPwn_Cols)] = UberPwnFrame_Attack_Color;
      elseif row == 1 and col == 2 then
        
      else
        local f = CreateFrame("Frame",nil,UberPwnFrame);
        f:SetWidth(UberPwn_Pixel_Size);
        f:SetHeight(UberPwn_Pixel_Size);

        local t = f:CreateTexture();
        t:SetColorTexture(1,1,1);
        t:SetAllPoints(f);
        t:SetBlendMode("DISABLE");
        f.texture = t;

        f:SetPoint("TOPLEFT",(col*UberPwn_Pixel_Size),(-row*UberPwn_Pixel_Size));
        f:Show();

        UberPwn_Pixels[col+((row-1)*UberPwn_Cols)] = t;
      end

    end
  end
  
  UberPwn_PaintValue(UberPwn_version, UberPwn_Pixels[3]);
end

UberPwn_UpdateInterval = .1; -- How often the OnUpdate code will run (in seconds)

-- Called every frame from the wow client
function UberPwn_OnUpdate(self, elapsed)
  self.TimeSinceLastUpdate = self.TimeSinceLastUpdate + elapsed;
  if (self.TimeSinceLastUpdate > UberPwn_UpdateInterval) then
    UberPwn_Update();
    self.TimeSinceLastUpdate = 0;
  end
end


function UberPwn_CreateMacro(macroName, macroBody)
  local name, iconTexture, body, isLocal = GetMacroInfo(macroName);
  if name == nil then
    CreateMacro(macroName, "INV_MISC_QUESTIONMARK", macroBody, nil, nil);
  end
end

function UberPwn_CreateAndBindMacro(macroName, macroBody, binding)
  UberPwn_CreateMacro(macroName, macroBody);
  SetBindingMacro(binding, macroName);
end

function UberPwn_Update()
  UberPwn_PaintDate(GetTime(), UberPwn_Pixels[8]);

  -- Check if target exists and is alive
  if UnitExists("target") and not UnitIsDead("target") then
    -- Target is alive, keep attack color as set by macro
  else
    -- No target or target is dead, turn off attack
    UberPwnFrame_Attack_Color:SetVertexColor(0,0,0);
  end

  local attackColor = math.floor(UberPwnFrame_Attack_Color:GetVertexColor() * 255 + 0.5)

  local pixelIndex = 21;

  for i=pixelIndex,#UberPwn_Pixels
  do
    UberPwn_Pixels[i]:SetVertexColor(1,1,1);
  end
end



function UberPwn_PaintDate(date, dateElement)
  if date ~= nil then
    local dateColor = UberPwn_toColor((date*1000)%10000000);
    dateElement:SetVertexColor(dateColor[1],dateColor[2],dateColor[3]);
  else
    dateElement:SetVertexColor(1,1,1);
  end
end

function UberPwn_PaintValue(value, element)
  if value ~= nil then
    local rgb = UberPwn_toColor(value);
    element:SetVertexColor(rgb[1],rgb[2],rgb[3]);
  else
    element:SetVertexColor(1,1,1);
  end
end

function UberPwn_toColor(num)
  local tmp = num;
  if num == nil then
    tmp = 16777215;
  else
    if tmp < 0 then
      tmp = 0;
    end
    
    if tmp > 16777214 then
      tmp = 16777214;
    end
  end
  
  local b = 0;
  if tmp >= 65536 then
    b = math.floor(tmp / 65536);
    tmp = math.fmod(tmp, 65536);
  end

  local g = 0;
  if tmp >= 256 then
    g = math.floor(tmp / 256);
    tmp = math.fmod(tmp, 256);
  end

  local r = tmp;

  return {r/255,g/255,b/255};
end
